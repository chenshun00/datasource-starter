package io.github.chenshun00.multi.datasource.transactional.support;

import org.springframework.jdbc.datasource.ConnectionHandle;
import org.springframework.jdbc.datasource.ConnectionHolder;

import java.sql.Connection;

/**
 * @author luobo.cs@raycloud.com
 * @since 2021/6/7 9:42 上午
 */
public class MyConnectionHolder extends ConnectionHolder {

    public MyConnectionHolder(ConnectionHandle connectionHandle) {
        super(connectionHandle);
    }

    public MyConnectionHolder(Connection connection) {
        super(connection);
    }

    public MyConnectionHolder(Connection connection, boolean transactionActive) {
        super(connection, transactionActive);
    }

    @Override
    public boolean hasConnection() {
        return super.hasConnection();
    }

    @Override
    public void setTransactionActive(boolean transactionActive) {
        super.setTransactionActive(transactionActive);
    }

    @Override
    public boolean isTransactionActive() {
        return super.isTransactionActive();
    }

    @Override
    public void setConnection(Connection connection) {
        super.setConnection(connection);
    }
}
