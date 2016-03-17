package com.revolut.service.impl;

import java.util.Arrays;
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

		log.info("Service {} started", MoneyTransferServiceImpl.class);
	}

	@Override
	public MoneyTransfer transfer(String profileIdFrom, String profileIdTo, Money amount) {

		log.info("About to transfer {} from {} to {}", amount, profileIdFrom, profileIdTo);

		Transaction transaction = transactionManager.begin();

		try {

			obtainLocks(transaction, profileIdFrom, profileIdTo);

			Optional<Profile> optionalFrom = profileService.withTransaction(transaction).findProfile(profileIdFrom);
			Optional<Profile> optionalTo = profileService.withTransaction(transaction).findProfile(profileIdTo);

			Preconditions.checkArgument(optionalFrom.isPresent(), "profileFrom %s is not found", profileIdFrom);
			Preconditions.checkArgument(optionalTo.isPresent(), "profileTo %s is not found", profileIdTo);

			Profile profileFrom = optionalFrom.get();
			Profile profileTo = optionalTo.get();

			FxRate fxRate = fxRatesProvider.getCurrentRate(profileFrom.getCurrency(), profileTo.getCurrency());
			Money fee = feeCalculator.calculateFee(profileFrom, profileTo, amount);
			Money convertedAmount = FxConverter.convert(amount.subtractMoney(fee), fxRate);

			MoneyTransfer moneyTransfer = new MoneyTransfer(profileFrom, profileTo, amount, convertedAmount, fee,
					fxRate);

			profileService.withTransaction(transaction).subtractMoney(profileFrom, moneyTransfer.getMoneyFrom());
			profileService.withTransaction(transaction).addMoney(profileTo, moneyTransfer.getMoneyTo());

			// We can also add fee to revolut account here...

			transactionManager.commit(transaction);

			log.info("Successfully transfered {} ", moneyTransfer);

			return moneyTransfer;

		} catch (Throwable t) {

			log.error("{}: {}", transaction, t.getMessage(), t);

			transactionManager.rollback(transaction);

			throw t;
		}

	}

	private void obtainLocks(Transaction transaction, String profileIdFrom, String profileIdTo) {

		String[] locks = new String[] { profileIdFrom, profileIdTo };

		Arrays.sort(locks);

		for (String lock : locks) {
			profileService.withTransaction(transaction).obtainLock(lock);
		}

	}

}
