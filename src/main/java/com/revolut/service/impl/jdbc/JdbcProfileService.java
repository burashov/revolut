package com.revolut.service.impl.jdbc;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Currency;
import java.util.Optional;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.revolut.model.Money;
import com.revolut.model.Profile;
import com.revolut.service.ProfileService;
import com.revolut.service.ServiceException;
import com.revolut.service.ServiceException.Type;
import com.revolut.transaction.Transaction;
import com.revolut.transaction.TransactionManager;
import com.revolut.transaction.impl.jdbc.JdbcTransaction;

public class JdbcProfileService implements ProfileService {

	private QueryRunner queryRunner = new QueryRunner();

	private final static String SELECT_PROFILE_SQL = "select * from profiles where profileId = ?";

	private final static String SELECT_PROFILE_FOR_UPDATE_SQL = "select * from profiles where profileId = ? for update";

	private final static String ADD_MONEY_SQL = "update profiles set amount = amount + ? where profileId = ?";

	private final static String SUBTRACT_MONEY_SQL = "update profiles set amount = amount - ? where profileId = ?";

	private final static String CREATE_PROFILE_SQL = "insert into profiles (profileId, amount, currency) values (?, ?, ?)";

	private final static String COUNT_PROFILE_SQL = "select count(*) from profiles";

	private TransactionManager transactionManager;

	private Optional<Transaction> transaction;

	Logger log = LoggerFactory.getLogger(JdbcProfileService.class);

	public JdbcProfileService(TransactionManager transactionManager, Optional<Transaction> transaction) {
		Preconditions.checkNotNull(transactionManager);
		Preconditions.checkNotNull(transaction);

		this.transactionManager = transactionManager;
		this.transaction = transaction;
	}

	@Override
	public ProfileService withTransaction(Transaction transaction) {
		return new JdbcProfileService(transactionManager, Optional.of(transaction));
	}

	@Override
	public void obtainLock(String profileId) {
		JdbcTransaction transaction = getTransaction();

		try {
			Connection connection = transaction.getConnection();

			queryRunner.query(connection, SELECT_PROFILE_FOR_UPDATE_SQL, new ProfileResultSetHander(), profileId);

			log.info("{}: locked {}", transaction, profileId);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			releaseTransaction(transaction);
		}
	}

	@Override
	public Optional<Profile> findProfile(String profileId) {
		JdbcTransaction transaction = getTransaction();

		try {
			Connection connection = transaction.getConnection();

			log.info("{}: finding {}", transaction, profileId);

			return queryRunner.query(connection, SELECT_PROFILE_SQL, new ProfileResultSetHander(), profileId);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			releaseTransaction(transaction);
		}
	}

	private static class ProfileResultSetHander implements ResultSetHandler<Optional<Profile>> {

		@Override
		public Optional<Profile> handle(ResultSet rs) throws SQLException {
			if (rs.next()) {
				String profileId = rs.getString("profileId");
				String currency = rs.getString("currency");
				BigDecimal amount = rs.getBigDecimal("amount");

				Money money = new Money(Currency.getInstance(currency), amount);

				return Optional.of(new Profile(profileId, money));
			} else {
				return Optional.empty();
			}
		}

	}

	@Override
	public void addMoney(Profile profile, Money money) throws ServiceException {
		if (!profile.getCurrency().equals(money.getCurrency())) {
			throw new ServiceException("profile " + profile + " and money " + money + " must be in the same currency",
					Type.PROFILE_AND_MONEY_MUST_BE_IN_THE_SAME_CURRENCY);
		}

		JdbcTransaction transaction = getTransaction();

		try {
			Connection connection = transaction.getConnection();

			log.info("{}: Adding {} to {}", transaction, money, profile);

			queryRunner.update(connection, ADD_MONEY_SQL, money.getAmount(), profile.getId());
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			releaseTransaction(transaction);
		}
	}

	@Override
	public void subtractMoney(Profile profile, Money money) throws ServiceException {
		if (!profile.getCurrency().equals(money.getCurrency())) {
			throw new ServiceException("profile " + profile + " and money " + money + " must be in the same currency",
					Type.PROFILE_AND_MONEY_MUST_BE_IN_THE_SAME_CURRENCY);
		}
		if (profile.getMoney().getAmount().compareTo(money.getAmount()) == -1) {
			throw new ServiceException("profile " + profile + " does not have enough money " + money,
					Type.PROFILE_DOES_NOT_HAVE_ENOUGH_MONEY);
		}

		JdbcTransaction transaction = getTransaction();

		try {
			Connection connection = transaction.getConnection();

			log.info("{}: Subtracting {} from {}", transaction, money, profile);

			queryRunner.update(connection, SUBTRACT_MONEY_SQL, money.getAmount(), profile.getId());
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			releaseTransaction(transaction);
		}
	}

	@Override
	public void createProfile(Profile profile) throws ServiceException {
		Preconditions.checkNotNull(profile);

		if (profile.getMoney().getAmount().compareTo(BigDecimal.ZERO) == -1) {
			throw new ServiceException("profile " + profile + " does not have enough money " + profile.getMoney(),
					Type.PROFILE_DOES_NOT_HAVE_ENOUGH_MONEY);
		}

		JdbcTransaction transaction = getTransaction();

		try {
			Connection connection = transaction.getConnection();

			log.info("{}: Creating {}", transaction, profile);

			queryRunner.insert(connection, CREATE_PROFILE_SQL, new ResultSetHandler<Object>() {

				@Override
				public Object handle(ResultSet rs) throws SQLException {
					return null;
				}
			}, new Object[] { profile.getId(), profile.getMoney().getAmount(),
					profile.getCurrency().getCurrencyCode() });
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			releaseTransaction(transaction);
		}

	}

	@Override
	public long count() {

		JdbcTransaction transaction = getTransaction();

		try {
			Connection connection = transaction.getConnection();

			log.info("{}: counting", transaction);

			return queryRunner.query(connection, COUNT_PROFILE_SQL, new ResultSetHandler<Long>() {

				@Override
				public Long handle(ResultSet rs) throws SQLException {
					rs.next();
					Long count = rs.getLong(1);
					return count;
				}
			});

		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			releaseTransaction(transaction);
		}

	}

	private JdbcTransaction getTransaction() {

		if (transaction.isPresent()) {
			return (JdbcTransaction) transaction.get();
		} else {
			return (JdbcTransaction) transactionManager.begin();
		}
	}

	private void releaseTransaction(JdbcTransaction transaction) {

		if (this.transaction.isPresent()) {
			// do nothing
		} else {
			// close connection
			transactionManager.commit(transaction);
		}
	}

	public static class NoResultSetHandler implements ResultSetHandler<Object> {

		@Override
		public Object handle(ResultSet rs) throws SQLException {
			return null;
		}
	}
}
