package com.revolut.transaction.impl.jdbc;

import java.sql.Connection;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.revolut.transaction.Transaction;


public class JdbcTransaction implements Transaction {

	private Connection connection;
	
	private String id;
	
	public JdbcTransaction(Connection connection, String id) {
		
		Preconditions.checkNotNull(connection);
		Preconditions.checkNotNull(id);
		
		this.id = id;
		this.connection = connection;
	}
	
	public Connection getConnection() {
		return connection;
	}
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("id", id)
				.toString();
	}		
}
