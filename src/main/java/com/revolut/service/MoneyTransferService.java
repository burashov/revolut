package com.revolut.service;

import com.revolut.model.Money;
import com.revolut.model.MoneyTransfer;

public interface MoneyTransferService {
	
	MoneyTransfer transfer(String profileIdFrom, String profileIdTo, Money amount) throws ServiceException;
	
}
