package com.revolut.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

public class MoneyTransfer {

	@JsonProperty
	private String profileIdFrom;
	
	@JsonProperty
	private String profileIdTo;
		
	@JsonProperty
	private Money moneyFrom;
	
	@JsonProperty
	private Money moneyTo;
		
	@JsonProperty
	private Money fee;
	
	@JsonProperty
	private FxRate fxRate;

	public MoneyTransfer(Profile from, Profile to,
			Money moneyFrom, Money moneyTo, Money fee, FxRate fxRate) {
		Preconditions.checkNotNull(from);
		Preconditions.checkNotNull(to);
		Preconditions.checkNotNull(moneyFrom);
		Preconditions.checkNotNull(moneyTo);
		Preconditions.checkNotNull(fee);
		Preconditions.checkNotNull(fxRate);
		
		this.profileIdFrom = from.getId();
		this.profileIdTo = to.getId();
		this.moneyFrom = moneyFrom;
		this.moneyTo = moneyTo;
		this.fee = fee;
		this.fxRate = fxRate;
		
	}
	
	public String getProfileIdFrom() {
		return profileIdFrom;
	}

	public String getProfileIdTo() {
		return profileIdTo;
	}

	public Money getFee() {
		return fee;
	}

	public FxRate getFxRate() {
		return fxRate;
	}

	public Money getMoneyFrom() {
		return moneyFrom;
	}

	public Money getMoneyTo() {
		return moneyTo;
	}
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("profileIdFrom", profileIdFrom)
				.add("profileIdTo", profileIdTo)
				.add("moneyFrom", moneyFrom)
				.add("moneyTo", moneyTo)
				.add("fee", fee)
				.add("fxRate", fxRate)
				.toString();
	}		
	
}
