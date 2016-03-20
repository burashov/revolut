package com.revolut.model;

import java.util.Currency;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

public class Profile {

	@JsonProperty
	private String profileId;
	
	@JsonProperty
	private Money money;
	
	Profile() {		
	}
	
	public Profile(String profileId, Money money) {
		Preconditions.checkNotNull(profileId);
		Preconditions.checkNotNull(money);		
		
		this.profileId = profileId;
		this.money = money;
	}
	
	public String getId() {
		return profileId;
	}
	
	public Money getMoney() {
		return money;
	}
	
	public Currency getCurrency() {
		return money.getCurrency();
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("profileId", profileId)
				.add("money", money)
				.toString();
	}		
}
