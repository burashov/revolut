package com.revolut.model;

import java.math.BigDecimal;
import java.util.Currency;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

public class Money {

	private Currency currency;

	private BigDecimal amount;

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
