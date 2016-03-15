package com.revolut.service;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.revolut.config.ApplicationConfig;
import com.revolut.config.jdbc.JdbcApplicatonConfig;
import com.revolut.model.Money;
import com.revolut.model.Profile;

public class MoneyTransferTest {

	private ExecutorService executor = Executors.newFixedThreadPool(32);

	private Logger log = LoggerFactory.getLogger(MoneyTransferTest.class);
	
	@Test
	public void test() throws InterruptedException {
		ApplicationConfig config = new JdbcApplicatonConfig();
		config.load();

		ProfileService profileService = config.getProfileService();
		MoneyTransferService moneyTransferService = config.getMoneyTransferService();

		Profile profileFrom = new Profile("1", new Money(Currency.getInstance("GBP"), BigDecimal.valueOf(1000)));
		Profile profileTo = new Profile("2", new Money(Currency.getInstance("USD"), BigDecimal.valueOf(0)));

		profileService.createProfile(profileFrom);
		profileService.createProfile(profileTo);

		for(int i = 0; i < 1000; i++) {
			executor.submit(() -> moneyTransferService.transfer(profileFrom.getId(), profileTo.getId(),
				new Money(Currency.getInstance("GBP"), BigDecimal.ONE)));
		}
		
		executor.shutdown();
		executor.awaitTermination(1, TimeUnit.SECONDS);
		
		/*profileFrom = profileService.findProfile("1");
		profileTo = profileService.findProfile("2");
		*/

	}

}
