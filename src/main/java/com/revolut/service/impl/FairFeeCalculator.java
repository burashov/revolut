package com.revolut.service.impl;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.revolut.model.Money;
import com.revolut.model.Profile;
import com.revolut.service.FeeCalculator;

public class FairFeeCalculator implements FeeCalculator {

	Logger log = LoggerFactory.getLogger(FairFeeCalculator.class);
	
	public FairFeeCalculator() {
		log.info("Serivce {} started", FairFeeCalculator.class);
	}
	
	@Override
	public Money calculateFee(Profile from, Profile to, Money amount) {
		
		return new Money(amount.getCurrency(), amount.getAmount().divide(BigDecimal.TEN));
		
	}

}
