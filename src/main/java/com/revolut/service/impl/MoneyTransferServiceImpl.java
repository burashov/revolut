package com.revolut.service.impl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.revolut.model.FxConverter;
import com.revolut.model.FxRate;
import com.revolut.model.Money;
import com.revolut.model.MoneyTransfer;
import com.revolut.model.Profile;
import com.revolut.service.FeeCalculator;
import com.revolut.service.FxRatesProvider;
import com.revolut.service.MoneyTransferService;
import com.revolut.service.ProfileService;
import com.revolut.transaction.Transaction;
import com.revolut.transaction.TransactionManager;

public class MoneyTransferServiceImpl implements MoneyTransferService {

	private FxRatesProvider fxRatesProvider;

	private FeeCalculator feeCalculator;

	private TransactionManager transactionManager;

	private ProfileService profileService;

	private Logger log = LoggerFactory.getLogger(MoneyTransferServiceImpl.class);

	public MoneyTransferServiceImpl(FxRatesProvider fxRatesProvider, FeeCalculator feeCalculator,
			TransactionManager transactionManager, ProfileService profileService) {

		Preconditions.checkNotNull(fxRatesProvider);
		Preconditions.checkNotNull(feeCalculator);
		Preconditions.checkNotNull(transactionManager);
		Preconditions.checkNotNull(profileService);

		this.fxRatesProvider = fxRatesProvider;
		this.feeCalculator = feeCalculator;
		this.transactionManager = transactionManager;
		this.profileService = profileService;

	}

	@Override
	public MoneyTransfer transfer(String profileIdFrom, String profileIdTo, Money amount) {

		Transaction transaction = transactionManager.begin();

		try {
			Optional<Profile> optionalFrom = profileService.findProfile(transaction, profileIdFrom);
			Optional<Profile> optionalTo = profileService.findProfile(transaction, profileIdTo);

			Preconditions.checkArgument(optionalFrom.isPresent(), "profileFrom %s is not found", profileIdFrom);
			Preconditions.checkArgument(optionalTo.isPresent(), "profileTo %s is not found", profileIdTo);

			Profile profileFrom = optionalFrom.get();
			Profile profileTo = optionalTo.get();

			FxRate fxRate = fxRatesProvider.getCurrentRate(profileFrom.getCurrency(), profileTo.getCurrency());
			Money fee = feeCalculator.calculateFee(profileFrom, profileTo, amount);
			Money convertedAmount = FxConverter.convert(amount.subtractMoney(fee), fxRate);

			MoneyTransfer moneyTransfer = new MoneyTransfer(profileFrom, profileTo, amount, convertedAmount, fee,
					fxRate);

			profileService.subtractMoney(transaction, profileFrom, moneyTransfer.getMoneyFrom());
			profileService.addMoney(transaction, profileTo, moneyTransfer.getMoneyTo());

			// We can also add fee to revolut account here...

			transactionManager.commit(transaction);

			System.out.println("123213");
			log.info("Successfully transfered {} ", moneyTransfer);

			return moneyTransfer;

		} catch (Throwable t) {
			transactionManager.rollback(transaction);
			throw t;
		}

	}

}
