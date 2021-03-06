package com.revolut.model;

import java.math.BigDecimal;
import java.util.Currency;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

public class Money {

	@JsonProperty
	private Currency currency;

	@JsonProperty
	private BigDecimal amount;

	Money() {
	}
	
	public Money(Currency currency, BigDecimal amount) {
		Preconditions.checkNotNull(currency);
		Preconditions.checkNotNull(amount);
		Preconditions.checkArgument(amount.signum() != -1);

		this.currency = currency;
		this.amount = amount;
	}

	public Money addMoney(Money money) {
		Preconditions.checkNotNull(money);
		Preconditions.checkArgument(money.getCurrency().equals(currency));
		
		return new Money(currency, amount.add(money.amount));
	}
	
	public Money subtractMoney(Money money) {
		Preconditions.checkNotNull(money);
		Preconditions.checkArgument(money.getCurrency().equals(currency));
		
		return new Money(currency, amount.subtract(money.amount));
	}
	
	
	public Currency getCurrency() {
		return currency;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("currency", currency)
				.add("amount", amount)
				.toString();
	}	
}
