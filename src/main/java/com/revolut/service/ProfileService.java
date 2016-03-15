package com.revolut.service;

import java.util.Optional;

import com.revolut.model.Money;
import com.revolut.model.Profile;
import com.revolut.transaction.Transaction;

public interface ProfileService {

	Optional<Profile> findProfile(Transaction transaction, String profileId);
	
	void addMoney(Transaction transaction, Profile profile, Money money);

	void subtractMoney(Transaction transaction, Profile profile, Money money);
	
	void createProfile(Profile profile);
	
	long count();
}
