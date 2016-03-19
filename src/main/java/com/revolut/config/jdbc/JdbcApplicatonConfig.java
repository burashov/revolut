package com.revolut.config.jdbc;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;

import com.revolut.config.ApplicationConfig;
import com.revolut.service.FeeCalculator;
import com.revolut.service.FxRatesProvider;
import com.revolut.service.MoneyTransferService;
import com.revolut.service.ProfileService;
import com.revolut.service.impl.FairFeeCalculator;
import com.revolut.service.impl.MoneyTransferServiceImpl;
import com.revolut.service.impl.RandomFxRatesProvider;
import com.revolut.service.impl.jdbc.JdbcProfileService;
import com.revolut.transaction.TransactionManager;
import com.revolut.transaction.impl.jdbc.JdbcTransactionManager;

public class JdbcApplicatonConfig implements ApplicationConfig {

	private ProfileService profileService;

	private MoneyTransferService moneyTransferService;

	private TransactionManager transactionManager;

	private Optional<FxRatesProvider> fxRatesProvider = Optional.empty();

	public JdbcApplicatonConfig withFxRatesProvider(FxRatesProvider fxRatesProvider) {
		this.fxRatesProvider = Optional.of(fxRatesProvider);

		return this;
	}

	@Override
	public void refresh() {

		BasicDataSource dataSource = prepareJdbc();

		transactionManager = new JdbcTransactionManager(dataSource);

		FeeCalculator feeCalculator = new FairFeeCalculator();

		if (!fxRatesProvider.isPresent()) {
			fxRatesProvider = Optional.of(new RandomFxRatesProvider());
		}

		profileService = new JdbcProfileService(transactionManager, Optional.empty());
		moneyTransferService = new MoneyTransferServiceImpl(fxRatesProvider.get(), feeCalculator, transactionManager,
				profileService);

	}

	private BasicDataSource prepareJdbc() {
		try {
			DriverManager.getConnection("jdbc:derby:memory:revolut;drop=true");
		} catch (SQLException e1) {
			// ignore
		}

		BasicDataSource dataSource = new BasicDataSource();

		dataSource.setUrl("jdbc:derby:memory:revolut;create=true");

		QueryRunner queryRunner = new QueryRunner(dataSource);

		try {
			queryRunner.update("create table profiles(profileId varchar(36) primary key, "
					+ "currency char(3) not null, " + "amount decimal(30, 4) not null)");

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return dataSource;
	}

	@Override
	public ProfileService getProfileService() {
		return profileService;
	}

	@Override
	public MoneyTransferService getMoneyTransferService() {
		return moneyTransferService;
	}

	@Override
	public TransactionManager getTransactionManager() {
		return transactionManager;
	}

}
