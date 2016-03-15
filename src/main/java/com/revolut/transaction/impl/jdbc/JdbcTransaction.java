package com.revolut.transaction.impl.jdbc;

import java.sql.Connection;

import com.revolut.transaction.Transaction;


public class JdbcTransaction implements Transaction {

	private Connection connection;

	public JdbcTransaction(Connection connection) {
		this.connection = connection;
	}
	
	public Connection getConnection() {
		return connection;
	}
	
}
