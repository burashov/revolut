package com.revolut.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

import org.junit.Assert;
import org.junit.Test;

import com.revolut.config.ApplicationConfig;
import com.revolut.config.jdbc.JdbcApplicatonConfig;
import com.revolut.model.Money;
import com.revolut.model.MoneyTransfer;
import com.revolut.model.Profile;

public class MainLogicTest {

	@Test
	public void test() throws InterruptedException {
		ApplicationConfig config = new JdbcApplicatonConfig();
		config.refresh();

		ProfileService profileService = config.getProfileService();
		MoneyTransferService moneyTransferService = config.getMoneyTransferService();

		Profile profile1 = new Profile("1", new Money(Currency.getInstance("GBP"), BigDecimal.valueOf(1000)));
		Profile profile2 = new Profile("2", new Money(Currency.getInstance("USD"), BigDecimal.valueOf(0)));

		profileService.createProfile(profile1);
		profileService.createProfile(profile2);

		MoneyTransfer moneyTransfer = moneyTransferService.transfer(profile1.getId(), profile2.getId(),
				new Money(Currency.getInstance("GBP"), BigDecimal.ONE));

		profile1 = profileService.findProfile("1").get();
		profile2 = profileService.findProfile("2").get();

		BigDecimal toAmount = moneyTransfer.getMoneyTo().getAmount();
		BigDecimal fxRate = moneyTransfer.getFxRate().getRate();
		BigDecimal fee = moneyTransfer.getFee().getAmount();

		Assert.assertEquals(BigDecimal.ONE, toAmount.divide(fxRate).add(fee).setScale(0));
	}
}
