package com.revolut.service;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.Assert;

import com.revolut.config.ApplicationConfig;
import com.revolut.config.jdbc.JdbcApplicatonConfig;
import com.revolut.model.FxRate;
import com.revolut.model.Money;
import com.revolut.model.Profile;

public class TransactionalMoneyTransferTest {

	private ExecutorService executor = Executors.newFixedThreadPool(32);

	@Test
	public void test() throws InterruptedException, ServiceException {
		ApplicationConfig config = new JdbcApplicatonConfig()
				.withFxRatesProvider((Currency from, Currency to) -> new FxRate(from, to, BigDecimal.TEN));

		config.refresh();

		ProfileService profileService = config.getProfileService();
		MoneyTransferService moneyTransferService = config.getMoneyTransferService();

		Profile profile1 = new Profile("1", new Money(Currency.getInstance("GBP"), BigDecimal.valueOf(1000)));
		Profile profile2 = new Profile("2", new Money(Currency.getInstance("USD"), BigDecimal.valueOf(1000)));

		profileService.createProfile(profile1);
		profileService.createProfile(profile2);

		for (int i = 0; i < 1000; i++) {
			// test no deadlocks
			executor.submit(() -> moneyTransferService.transfer(profile1.getId(), profile2.getId(),
					new Money(Currency.getInstance("GBP"), BigDecimal.ONE)));
			executor.submit(() -> moneyTransferService.transfer(profile2.getId(), profile1.getId(),
					new Money(Currency.getInstance("USD"), BigDecimal.ONE)));
		}

		executor.shutdown();
		executor.awaitTermination(15, TimeUnit.SECONDS);
		
		Profile p1 = profileService.findProfile("1").get();
		Profile p2 = profileService.findProfile("2").get();
		
		// moneyFrom=Money{currency=USD, amount=1}, 
		// moneyTo=Money{currency=GBP, amount=9.0}, 
		// fee=Money{currency=USD, amount=0.1}, 
		// fxRate=FxRate{from=USD, to=GBP, rate=10}}
		// 
		// (1 - 0.1) * 10 * 1000 = 9000
		Assert.assertEquals(BigDecimal.valueOf(9000), p1.getMoney().getAmount().setScale(0));
		Assert.assertEquals(BigDecimal.valueOf(9000), p2.getMoney().getAmount().setScale(0));
	}

}
