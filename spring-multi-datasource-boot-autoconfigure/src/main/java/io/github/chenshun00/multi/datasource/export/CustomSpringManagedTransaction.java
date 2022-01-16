package io.github.chenshun00.multi.datasource.export;

import io.github.chenshun00.multi.datasource.transactional.support.MyConnectionHolder;
import io.github.chenshun00.multi.datasource.transactional.support.MyTransactionSynchronizationManager;
import org.apache.ibatis.transaction.Transaction;
import org.mybatis.logging.Logger;
import org.mybatis.logging.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.springframework.util.Assert.notNull;

/**
 * @author chenshun00@gmail.com
 * @since 2022/1/14 9:32 下午
 */
public class CustomSpringManagedTransaction implements Transaction {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomSpringManagedTransaction.class);

    private final DataSource dataSource;
    private final String name;

    private Connection connection;

    private boolean isConnectionTransactional;

    private boolean autoCommit;

    public CustomSpringManagedTransaction(DataSource dataSource, String name) {
        notNull(dataSource, "No DataSource specified");
        notNull(name, "No name specified");
        this.dataSource = dataSource;
        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Connection getConnection() throws SQLException {
        if (this.connection == null) {
            openConnection();
        }
        return this.connection;
    }

    /**
     * Gets a connection from Spring transaction manager and discovers if this {@code Transaction} should manage
     * connection or let it to Spring.
     * <p>
     * It also reads autocommit setting because when using Spring Transaction MyBatis thinks that autocommit is always
     * false and will always call commit/rollback so we need to no-op that calls.
     */
    private void openConnection() throws SQLException {
        this.connection = MyDataSourceUtils.getConnection(this.dataSource, this.name);
        this.autoCommit = this.connection.getAutoCommit();
        this.isConnectionTransactional = MyDataSourceUtils.isConnectionTransactional(this.connection, this.dataSource, this.name);

        LOGGER.debug(() -> "JDBC Connection [" + this.connection + "] will"
                + (this.isConnectionTransactional ? " " : " not ") + "be managed by Spring");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void commit() throws SQLException {
        if (this.connection != null && !this.isConnectionTransactional && !this.autoCommit) {
            LOGGER.debug(() -> "Committing JDBC Connection [" + this.connection + "]");
            this.connection.commit();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void rollback() throws SQLException {
        if (this.connection != null && !this.isConnectionTransactional && !this.autoCommit) {
            LOGGER.debug(() -> "Rolling back JDBC Connection [" + this.connection + "]");
            this.connection.rollback();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws SQLException {
        MyDataSourceUtils.releaseConnection(this.name, this.connection, this.dataSource);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getTimeout() throws SQLException {
        MyConnectionHolder holder = (MyConnectionHolder) MyTransactionSynchronizationManager.getResource(name, dataSource);
        if (holder != null && holder.hasTimeout()) {
            return holder.getTimeToLiveInSeconds();
        }
        return null;
    }

}
