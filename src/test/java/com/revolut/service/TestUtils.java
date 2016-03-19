package com.revolut.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

import com.revolut.model.Money;
import com.revolut.model.Profile;

public class TestUtils {

	private static final List<Currency> availableCurrencies = new ArrayList<>(Currency.getAvailableCurrencies());

	private TestUtils() {
		
	}
	
	public static Profile createRandomProfile() {
		String profileId = UUID.randomUUID().toString();
		
		BigDecimal amount = BigDecimal.valueOf(Math.random() * 1000);

		int i = (int) (Math.random() * availableCurrencies.size());

		Money money = new Money(availableCurrencies.get(i), amount);

		return new Profile(profileId, money);
	}

}
