package com.revolut.service;

import java.math.BigDecimal;
import java.util.Currency;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.revolut.config.ApplicationConfig;
import com.revolut.config.jdbc.JdbcApplicatonConfig;
import com.revolut.model.Money;
import com.revolut.model.MoneyTransfer;
import com.revolut.model.Profile;

public class MainLogicTest {

	private ProfileService profileService;
	private MoneyTransferService moneyTransferService;

	private Profile profile1;
	private Profile profile2;

	@Before
	public void before() throws ServiceException {
		ApplicationConfig config = new JdbcApplicatonConfig();
		config.refresh();

		profileService = config.getProfileService();
		moneyTransferService = config.getMoneyTransferService();

		profile1 = new Profile("1", new Money(Currency.getInstance("GBP"), BigDecimal.valueOf(1000)));
		profile2 = new Profile("2", new Money(Currency.getInstance("USD"), BigDecimal.valueOf(0)));

		profileService.createProfile(profile1);
		profileService.createProfile(profile2);
	}

	@Test
	public void testMoneyTransfer() throws ServiceException {

		MoneyTransfer moneyTransfer = moneyTransferService.transfer(profile1.getId(), profile2.getId(),
				new Money(Currency.getInstance("GBP"), BigDecimal.ONE));

		profile1 = profileService.findProfile("1").get();
		profile2 = profileService.findProfile("2").get();

		BigDecimal toAmount = moneyTransfer.getMoneyTo().getAmount();
		BigDecimal fxRate = moneyTransfer.getFxRate().getRate();
		BigDecimal fee = moneyTransfer.getFee().getAmount();

		Assert.assertEquals(BigDecimal.ONE, toAmount.divide(fxRate).add(fee).setScale(0));
	}
	
	@Test
	public void testZeroMoney() throws ServiceException {

		moneyTransferService.transfer(profile1.getId(), profile2.getId(),
				new Money(Currency.getInstance("GBP"), BigDecimal.valueOf(1000)));

		profile1 = profileService.findProfile("1").get();

		Assert.assertEquals(BigDecimal.ZERO, profile1.getMoney().getAmount().setScale(0));
	}	

	@Test
	public void testNoEnoughMoney() {

		try {
			moneyTransferService.transfer(profile1.getId(), profile2.getId(),
					new Money(Currency.getInstance("GBP"), BigDecimal.valueOf(1001)));
		} catch (ServiceException e) {
			Assert.assertEquals(ServiceException.Type.PROFILE_DOES_NOT_HAVE_ENOUGH_MONEY, e.getType());
		}
	}
	
	@Test
	public void testProfileDoesNotExist() {

		try {
			moneyTransferService.transfer(profile1.getId(), "3",
					new Money(Currency.getInstance("GBP"), BigDecimal.ONE));
		} catch (ServiceException e) {
			Assert.assertEquals(ServiceException.Type.PROFILE_DOES_NOT_EXIST, e.getType());
		}
	}	

	@Test
	public void testDifferentCurrency() {

		try {
			moneyTransferService.transfer(profile1.getId(), profile2.getId(),
					new Money(Currency.getInstance("ZAR"), BigDecimal.ONE));
		} catch (ServiceException e) {
			Assert.assertEquals(ServiceException.Type.PROFILE_AND_MONEY_MUST_BE_IN_THE_SAME_CURRENCY, e.getType());
		}
	}	

	
	@Test(expected=RuntimeException.class)
	public void testNoNegativeMoneyPossible() {
		new Money(Currency.getInstance("GBP"),BigDecimal.valueOf(-1));
	}
}
