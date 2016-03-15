package com.revolut.transaction;

public interface TransactionManager {

	Transaction begin();
	
	void rollback(Transaction transaction);
	
	void commit(Transaction transaction);
	
}
