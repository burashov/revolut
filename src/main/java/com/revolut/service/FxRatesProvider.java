package com.revolut.service;

import java.util.Currency;

import com.revolut.model.FxRate;

public interface FxRatesProvider {

	FxRate getCurrentRate(Currency from, Currency to);
	
}
