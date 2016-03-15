package com.revolut.service.impl.jdbc;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Currency;
import java.util.Optional;

import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;

import com.google.common.base.Preconditions;
import com.revolut.model.Money;
import com.revolut.model.Profile;
import com.revolut.service.ProfileService;
import com.revolut.transaction.Transaction;
import com.revolut.transaction.impl.jdbc.JdbcTransaction;

public class JdbcProfileService implements ProfileService {

	private QueryRunner queryRunner = new QueryRunner();

	private QueryRunner autoCommitQueryRunner;

	private final static String SELECT_PROFILE_SQL = "select * from profiles where p.profileId = ?";

	private final static String ADD_MONEY_SQL = "update profiles set amount = amount + ? where accountId = ?";

	private final static String SUBTRACT_MONEY_SQL = "update profiles set amount = amount - ? where accountId = ?";

	private final static String CREATE_PROFILE_SQL = "insert into profiles (profileId, amount, currency) values (?, ?, ?)";

	private final static String COUNT_PROFILE_SQL = "select count(*) from profiles";

	public JdbcProfileService(DataSource dataSource) {
		Preconditions.checkNotNull(dataSource);

		this.autoCommitQueryRunner = new QueryRunner(dataSource);
	}

	@Override
	public Optional<Profile> findProfile(Transaction transaction, String profileId) {
		Connection connection = ((JdbcTransaction) transaction).getConnection();

		try {
			return queryRunner.query(connection, SELECT_PROFILE_SQL, new ProfileResultSetHander(), profileId);
		} catch (SQLException e) {
			throw new RuntimeException(e);
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
	public void addMoney(Transaction transaction, Profile profile, Money money) {
		Preconditions.checkArgument(profile.getCurrency().equals(money.getCurrency()),
				"account %s and money %s must be in the same currency");

		Connection connection = ((JdbcTransaction) transaction).getConnection();

		try {
			queryRunner.update(connection, ADD_MONEY_SQL, money.getAmount());
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void subtractMoney(Transaction transaction, Profile profile, Money money) {
		Preconditions.checkArgument(profile.getCurrency().equals(money.getCurrency()),
				"account %s and money %s must be in the same currency");
		Preconditions.checkArgument(profile.getMoney().getAmount().compareTo(money.getAmount()) != -1,
				"account %s does not have enough money %s", money);

		Connection connection = ((JdbcTransaction) transaction).getConnection();

		try {
			queryRunner.update(connection, SUBTRACT_MONEY_SQL, money.getAmount());
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void createProfile(Profile profile) {
		Preconditions.checkNotNull(profile);

		try {
			autoCommitQueryRunner.insert(CREATE_PROFILE_SQL, new ResultSetHandler<Object>() {

				@Override
				public Object handle(ResultSet rs) throws SQLException {
					return null;
				}
			}, new Object[] { profile.getId(), profile.getMoney().getAmount(),  profile.getCurrency().getCurrencyCode()});

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public long count() {
		try {
			return autoCommitQueryRunner.query(COUNT_PROFILE_SQL, new ResultSetHandler<Long>() {

				@Override
				public Long handle(ResultSet rs) throws SQLException {
					rs.next();
					Long count = rs.getLong(1);
					return count;
				}
			});

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}
}
