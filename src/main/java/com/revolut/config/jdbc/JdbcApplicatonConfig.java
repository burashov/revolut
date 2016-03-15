package com.revolut.config.jdbc;

import java.sql.SQLException;

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

	@Override
	public void load() {

		BasicDataSource dataSource = prepareJdbc();

		TransactionManager transactionManager = new JdbcTransactionManager(dataSource);

		FeeCalculator feeCalculator = new FairFeeCalculator();
		FxRatesProvider fxRatesProvider = new RandomFxRatesProvider();

		this.profileService = new JdbcProfileService(dataSource);
		this.moneyTransferService = new MoneyTransferServiceImpl(fxRatesProvider, feeCalculator, transactionManager,
				profileService);

	}

	private BasicDataSource prepareJdbc() {
		BasicDataSource dataSource = new BasicDataSource();

		dataSource.setUrl("jdbc:derby:memory:revolut;create=true");
		dataSource.setDefaultAutoCommit(true);
		
		QueryRunner queryRunner = new QueryRunner(dataSource);

		try {
			queryRunner.update("create table profiles(profileId char(36), currency char(36), amount decimal)");
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return dataSource;
	}

	@Override
	public ProfileService getProfileService() {
		return this.profileService;
	}

	@Override
	public MoneyTransferService getMoneyTransferService() {
		return this.moneyTransferService;
	}
}
