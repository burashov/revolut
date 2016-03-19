package com.revolut.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;

import com.revolut.config.ApplicationConfig;
import com.revolut.config.jdbc.JdbcApplicatonConfig;

public class ProfileCreationTest {

	private ExecutorService executor = Executors.newFixedThreadPool(32);

	@Test
	public void test() throws InterruptedException, ServiceException {
		ApplicationConfig config = new JdbcApplicatonConfig();
		config.refresh();

		ProfileService profileService = config.getProfileService();

		long count = profileService.count();
		Assert.assertEquals(0, count);

		for (int i = 0; i < 1000; i++) {

			executor.submit(new Runnable() {
				public void run() {
					try {
						profileService.createProfile(TestUtils.createRandomProfile());
					} catch (ServiceException e) {
						throw new RuntimeException(e);
					}
				}
			});
		}

		executor.shutdown();
		executor.awaitTermination(5, TimeUnit.SECONDS);

		count = profileService.count();
		Assert.assertEquals(1000, count);
	}
}
