package com.revolut.service.impl;

import java.math.BigDecimal;
import java.util.Currency;

import com.revolut.model.FxRate;
import com.revolut.service.FxRatesProvider;

public class RandomFxRatesProvider implements FxRatesProvider {

	@Override
	public FxRate getCurrentRate(Currency from, Currency to) {

		int rate = (int) (Math.random() * 10 + 1);

		return new FxRate(from, to, BigDecimal.valueOf(rate));
	}

}
