package com.revolut.config;

import com.revolut.service.MoneyTransferService;
import com.revolut.service.ProfileService;
import com.revolut.transaction.TransactionManager;

public interface ApplicationConfig {
	
	void load();
	
	ProfileService getProfileService();
	
	MoneyTransferService getMoneyTransferService();
}
