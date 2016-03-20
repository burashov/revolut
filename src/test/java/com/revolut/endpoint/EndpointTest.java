package com.revolut.endpoint;

import static com.revolut.endpoint.Bootstrap.BASE_URI;

import java.math.BigDecimal;
import java.util.Currency;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revolut.model.Money;
import com.revolut.model.MoneyTransfer;
import com.revolut.model.Profile;

public class EndpointTest {
	
	private WebTarget webTarget;
	
	private ObjectMapper mapper = new ObjectMapper();
	
	@Before
	public void before() {
		new Bootstrap().start(BASE_URI);
		
		Client client = ClientBuilder.newClient();
		client.register(JacksonFeature.class);
		client.register(ObjectMapperProvider.class);
		
		webTarget = client.target(BASE_URI);
	}

	
	@Test
	public void simpleIntegrationTest() {
		Profile profile1 = createProfile("USD");
		Profile profile2 = createProfile("GBP");	
		
		Money money = new Money(Currency.getInstance("USD"), new BigDecimal("1.12"));
		
		MoneyTransfer moneyTransfer = transfer(profile1, profile2, money);
		
		profile1 = getProfile(profile1.getId());
		profile2 = getProfile(profile2.getId());
		
		BigDecimal toAmount = moneyTransfer.getMoneyTo().getAmount();
		BigDecimal fxRate = moneyTransfer.getFxRate().getRate();
		BigDecimal fee = moneyTransfer.getFee().getAmount();		
				
		Assert.assertEquals(new BigDecimal("1.12"), toAmount.divide(fxRate).add(fee).setScale(2));
		Assert.assertEquals(
				new BigDecimal("1000").subtract(new BigDecimal("1.12")).setScale(4), 
				profile1.getMoney().getAmount().setScale(4));
		Assert.assertEquals(
				new BigDecimal("1000").add(new BigDecimal("1.12").subtract(fee).multiply(fxRate)).setScale(4),
				profile2.getMoney().getAmount().setScale(4));
						
	}

	private Profile getProfile(String profileId) {
		return httpRequest(webTarget.path("profile/get/" + profileId), Profile.class);
	}

	private Profile createProfile(String currencyCode) {
		return httpRequest(webTarget.path("profile/create/" + currencyCode + "/1000"), Profile.class);
	}
	
	private MoneyTransfer transfer(Profile from, Profile to, Money money) {
		return httpRequest(
				webTarget.path(
						"profile/transfer/" + from.getId() + "/" + to.getId() + 
						"/" + money.getCurrency().getCurrencyCode() + "/" + money.getAmount()), 
				MoneyTransfer.class);
				
	}	
	

	private <T> T httpRequest(WebTarget webTarget, Class<T> result) {
		Response response = webTarget.request().get();
		
		Assert.assertEquals(200, response.getStatus());
		
		JsonPayload payload = response.readEntity(JsonPayload.class);
		
		Assert.assertTrue(payload.isOk());
		
		return mapper.convertValue(payload.getPayload(), result);
	}	
		
}
