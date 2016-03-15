package com.revolut.service.impl;

import java.math.BigDecimal;

import com.revolut.model.Money;
import com.revolut.model.Profile;
import com.revolut.service.FeeCalculator;

public class FairFeeCalculator implements FeeCalculator {

	@Override
	public Money calculateFee(Profile from, Profile to, Money amount) {
		
		return new Money(amount.getCurrency(), amount.getAmount().divide(BigDecimal.TEN));
		
	}

}
