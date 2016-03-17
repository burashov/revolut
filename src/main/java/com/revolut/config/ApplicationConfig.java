package com.revolut.config;

import com.revolut.service.MoneyTransferService;
import com.revolut.service.ProfileService;
import com.revolut.transaction.TransactionManager;

public interface ApplicationConfig {
	
	void refresh();
	
	ProfileService getProfileService();
	
	MoneyTransferService getMoneyTransferService();
	
	TransactionManager getTransactionManager();
}
