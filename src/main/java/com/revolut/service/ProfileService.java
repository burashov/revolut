package com.revolut.service;

import java.util.Optional;

import com.revolut.model.Money;
import com.revolut.model.Profile;
import com.revolut.transaction.Transaction;

public interface ProfileService {

	ProfileService withTransaction(Transaction transaction);
	
	void obtainLock(String profileId);
	
	Optional<Profile> findProfile(String profileId);
	
	void addMoney(Profile profile, Money money);

	void subtractMoney(Profile profile, Money money);
	
	void createProfile(Profile profile);
	
	long count();
}
