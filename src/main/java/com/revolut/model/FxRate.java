package com.revolut.model;

import java.math.BigDecimal;
import java.util.Currency;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

public class FxRate {

	private Currency from;
	
	private Currency to;
	
	private BigDecimal rate;

	public FxRate(Currency from, Currency to, BigDecimal rate) {
		Preconditions.checkNotNull(from);
		Preconditions.checkNotNull(to);
		Preconditions.checkNotNull(rate);
		Preconditions.checkArgument(rate.signum() > 0);
		
		this.from = from;
		this.to = to;
		this.rate = rate;
	}
	
	public Currency getCurrencyFrom() {
		return from;
	}

	public Currency getCurrencyTo() {
		return to;
	}

	public BigDecimal getRate() {
		return rate;
	}
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("from", from)
				.add("to", to)
				.add("rate", rate)
				.toString();
	}
}
