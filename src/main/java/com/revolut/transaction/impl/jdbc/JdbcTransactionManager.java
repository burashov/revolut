package com.revolut.transaction.impl.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.revolut.transaction.Transaction;
import com.revolut.transaction.TransactionManager;


public class JdbcTransactionManager implements TransactionManager {

	private DataSource dataSource;
	
	private Logger log = LoggerFactory.getLogger(JdbcTransactionManager.class);
		
	public JdbcTransactionManager(DataSource dataSource) {
		this.dataSource = dataSource;
		
		log.info("Transaction manager {} started", JdbcTransaction.class);
	}
	
	@Override
	public Transaction begin() {
		try {
			Connection connection = dataSource.getConnection();
			connection.setAutoCommit(false);
			connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ); // phantom reads are ok
			
			Transaction transaction = new JdbcTransaction(connection, connection.toString()); 
			
			log.info("{}: begin", transaction);
			
			return transaction; 
			
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
			
			log.info("{}: rollback", transaction);
			
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
			
			log.info("{}: commit", transaction);
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

}
