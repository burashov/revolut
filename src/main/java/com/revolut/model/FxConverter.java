package com.revolut.model;

import java.math.BigDecimal;

import com.google.common.base.Preconditions;

public class FxConverter {

	private FxConverter() {

	}

	public static Money convert(Money from, FxRate rate) {

		Preconditions.checkNotNull(from);
		Preconditions.checkNotNull(rate);
		Preconditions.checkArgument(from.getCurrency().equals(rate.getCurrencyFrom()));

		BigDecimal convertedAmount = from.getAmount().multiply(rate.getRate());

		return new Money(rate.getCurrencyTo(), convertedAmount);

	}

}
