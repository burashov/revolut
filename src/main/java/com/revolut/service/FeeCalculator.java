package com.revolut.service;

import com.revolut.model.Money;
import com.revolut.model.Profile;
	
public interface FeeCalculator {

	Money calculateFee(Profile from, Profile to, Money amount);
	
}
