package io.github.chenshun00.multi.datasource.export;

import io.github.chenshun00.multi.datasource.transactional.support.MyConnectionHolder;
import io.github.chenshun00.multi.datasource.transactional.support.MyTransactionSynchronizationManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.datasource.ConnectionProxy;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.DelegatingDataSource;
import org.springframework.jdbc.datasource.SmartDataSource;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author chenshun00@gmail.com
 * @since 2022/1/14 9:36 下午
 */
public class MyDataSourceUtils {
    public static final int CONNECTION_SYNCHRONIZATION_ORDER = 1000;
    private static final Log logger = LogFactory.getLog(DataSourceUtils.class);

    public MyDataSourceUtils() {
    }

    public static Connection getConnection(DataSource dataSource, String name) throws CannotGetJdbcConnectionException {
        try {
            return doGetConnection(dataSource, name);
        } catch (SQLException var2) {
            throw new CannotGetJdbcConnectionException("Could not get JDBC Connection", var2);
        }
    }

    public static Connection doGetConnection(DataSource dataSource, String name) throws SQLException {
        Assert.notNull(dataSource, "No DataSource specified");
        MyConnectionHolder conHolder = (MyConnectionHolder) MyTransactionSynchronizationManager.getResource(name, dataSource);
        if (conHolder == null || !conHolder.hasConnection() && !conHolder.isSynchronizedWithTransaction()) {
            logger.debug("Fetching JDBC Connection from DataSource");
            Connection con = dataSource.getConnection();
            if (MyTransactionSynchronizationManager.isSynchronizationActive(name)) {
                try {
                    MyConnectionHolder holderToUse = conHolder;
                    if (conHolder == null) {
                        holderToUse = new MyConnectionHolder(con);
                    } else {
                        conHolder.setConnection(con);
                    }

                    holderToUse.requested();
                    MyTransactionSynchronizationManager.registerSynchronization(name, new ConnectionSynchronization(name, holderToUse, dataSource));
                    holderToUse.setSynchronizedWithTransaction(true);
                    if (holderToUse != conHolder) {
                        MyTransactionSynchronizationManager.bindResource(name, dataSource, holderToUse);
                    }
                } catch (RuntimeException var4) {
                    releaseConnection(name, con, dataSource);
                    throw var4;
                }
            }

            return con;
        } else {
            conHolder.requested();
            if (!conHolder.hasConnection()) {
                logger.debug("Fetching resumed JDBC Connection from DataSource");
                conHolder.setConnection(dataSource.getConnection());
            }

            return conHolder.getConnection();
        }
    }

    public static Integer prepareConnectionForTransaction(Connection con, TransactionDefinition definition) throws SQLException {
        Assert.notNull(con, "No Connection specified");
        if (definition != null && definition.isReadOnly()) {
            Object exToCheck;
            try {
                if (logger.isDebugEnabled()) {
                    logger.debug("Setting JDBC Connection [" + con + "] read-only");
                }

                con.setReadOnly(true);
            } catch (SQLException var4) {
                for (exToCheck = var4; exToCheck != null; exToCheck = ((Throwable) exToCheck).getCause()) {
                    if (exToCheck.getClass().getSimpleName().contains("Timeout")) {
                        throw var4;
                    }
                }

                logger.debug("Could not set JDBC Connection read-only", var4);
            } catch (RuntimeException var5) {
                for (exToCheck = var5; exToCheck != null; exToCheck = ((Throwable) exToCheck).getCause()) {
                    if (exToCheck.getClass().getSimpleName().contains("Timeout")) {
                        throw var5;
                    }
                }

                logger.debug("Could not set JDBC Connection read-only", var5);
            }
        }

        Integer previousIsolationLevel = null;
        if (definition != null && definition.getIsolationLevel() != -1) {
            if (logger.isDebugEnabled()) {
                logger.debug("Changing isolation level of JDBC Connection [" + con + "] to " + definition.getIsolationLevel());
            }

            int currentIsolation = con.getTransactionIsolation();
            if (currentIsolation != definition.getIsolationLevel()) {
                previousIsolationLevel = currentIsolation;
                con.setTransactionIsolation(definition.getIsolationLevel());
            }
        }

        return previousIsolationLevel;
    }

    public static void resetConnectionAfterTransaction(Connection con, Integer previousIsolationLevel) {
        Assert.notNull(con, "No Connection specified");

        try {
            if (previousIsolationLevel != null) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Resetting isolation level of JDBC Connection [" + con + "] to " + previousIsolationLevel);
                }

                con.setTransactionIsolation(previousIsolationLevel);
            }

            if (con.isReadOnly()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Resetting read-only flag of JDBC Connection [" + con + "]");
                }

                con.setReadOnly(false);
            }
        } catch (Throwable var3) {
            logger.debug("Could not reset JDBC Connection after transaction", var3);
        }

    }

    public static boolean isConnectionTransactional(Connection con, DataSource dataSource, String name) {
        if (dataSource == null) {
            return false;
        } else {
            MyConnectionHolder conHolder = (MyConnectionHolder) MyTransactionSynchronizationManager.getResource(name, dataSource);
            return conHolder != null && connectionEquals(conHolder, con);
        }
    }

    public static void applyTransactionTimeout(Statement stmt, String name, DataSource dataSource) throws SQLException {
        applyTimeout(stmt, name, dataSource, -1);
    }

    public static void applyTimeout(Statement stmt, String name, DataSource dataSource, int timeout) throws SQLException {
        Assert.notNull(stmt, "No Statement specified");
        MyConnectionHolder holder = null;
        if (dataSource != null) {
            holder = (MyConnectionHolder) MyTransactionSynchronizationManager.getResource(name, dataSource);
        }

        if (holder != null && holder.hasTimeout()) {
            stmt.setQueryTimeout(holder.getTimeToLiveInSeconds());
        } else if (timeout >= 0) {
            stmt.setQueryTimeout(timeout);
        }

    }

    public static void releaseConnection(String name, Connection con, DataSource dataSource) {
        try {
            doReleaseConnection(name, con, dataSource);
        } catch (SQLException var3) {
            logger.debug("Could not close JDBC Connection", var3);
        } catch (Throwable var4) {
            logger.debug("Unexpected exception on closing JDBC Connection", var4);
        }

    }

    public static void doReleaseConnection(String name, Connection con, DataSource dataSource) throws SQLException {
        if (con != null) {
            if (dataSource != null) {
                MyConnectionHolder conHolder = (MyConnectionHolder) MyTransactionSynchronizationManager.getResource(name, dataSource);
                if (conHolder != null && connectionEquals(conHolder, con)) {
                    conHolder.released();
                    return;
                }
            }

            doCloseConnection(con, dataSource);
        }
    }

    public static void doCloseConnection(Connection con, DataSource dataSource) throws SQLException {
        if (!(dataSource instanceof SmartDataSource) || ((SmartDataSource) dataSource).shouldClose(con)) {
            con.close();
        }

    }

    private static boolean connectionEquals(MyConnectionHolder conHolder, Connection passedInCon) {
        if (!conHolder.hasConnection()) {
            return false;
        } else {
            Connection heldCon = conHolder.getConnection();
            return heldCon == passedInCon || heldCon.equals(passedInCon) || getTargetConnection(heldCon).equals(passedInCon);
        }
    }

    public static Connection getTargetConnection(Connection con) {
        Connection conToUse;
        for (conToUse = con; conToUse instanceof ConnectionProxy; conToUse = ((ConnectionProxy) conToUse).getTargetConnection()) {
        }

        return conToUse;
    }

    private static int getConnectionSynchronizationOrder(DataSource dataSource) {
        int order = 1000;

        for (DataSource currDs = dataSource; currDs instanceof DelegatingDataSource; currDs = ((DelegatingDataSource) currDs).getTargetDataSource()) {
            --order;
        }

        return order;
    }

    private static class ConnectionSynchronization extends TransactionSynchronizationAdapter {
        private final MyConnectionHolder connectionHolder;
        private final DataSource dataSource;
        private int order;
        private final String name;
        private boolean holderActive = true;

        public ConnectionSynchronization(String name, MyConnectionHolder connectionHolder, DataSource dataSource) {
            this.name = name;
            this.connectionHolder = connectionHolder;
            this.dataSource = dataSource;
            this.order = MyDataSourceUtils.getConnectionSynchronizationOrder(dataSource);
        }

        public int getOrder() {
            return this.order;
        }

        public void suspend() {
            if (this.holderActive) {
                MyTransactionSynchronizationManager.unbindResource(name, this.dataSource);
                if (this.connectionHolder.hasConnection() && !this.connectionHolder.isOpen()) {
                    MyDataSourceUtils.releaseConnection(name, this.connectionHolder.getConnection(), this.dataSource);
                    this.connectionHolder.setConnection(null);
                }
            }

        }

        public void resume() {
            if (this.holderActive) {
                MyTransactionSynchronizationManager.bindResource(name, this.dataSource, this.connectionHolder);
            }

        }

        public void beforeCompletion() {
            if (!this.connectionHolder.isOpen()) {
                MyTransactionSynchronizationManager.unbindResource(name, this.dataSource);
                this.holderActive = false;
                if (this.connectionHolder.hasConnection()) {
                    MyDataSourceUtils.releaseConnection(this.name, this.connectionHolder.getConnection(), this.dataSource);
                }
            }

        }

        public void afterCompletion(int status) {
            if (this.holderActive) {
                MyTransactionSynchronizationManager.unbindResourceIfPossible(this.name, this.dataSource);
                this.holderActive = false;
                if (this.connectionHolder.hasConnection()) {
                    MyDataSourceUtils.releaseConnection(name, this.connectionHolder.getConnection(), this.dataSource);
                    this.connectionHolder.setConnection((Connection) null);
                }
            }

            this.connectionHolder.reset();
        }
    }
}
