package com.revolut.transaction.impl.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.revolut.transaction.Transaction;
import com.revolut.transaction.TransactionManager;

public class JdbcTransactionManager implements TransactionManager {

	private DataSource dataSource;
	
	public JdbcTransactionManager(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	@Override
	public Transaction begin() {
		try {
			Connection connection = dataSource.getConnection();
			
			return new JdbcTransaction(connection);
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void rollback(Transaction transaction) {
		try {
			Connection connection = ((JdbcTransaction) transaction).getConnection();
			connection.rollback();
			connection.close(); // release connection to connection pool
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void commit(Transaction transaction) {
		try {
			Connection connection = ((JdbcTransaction) transaction).getConnection();
			connection.commit();
			connection.close(); // release connection to connection pool
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

}
