package com.revolut.endpoint;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.revolut.model.Money;
import com.revolut.model.MoneyTransfer;
import com.revolut.model.Profile;
import com.revolut.service.MoneyTransferService;
import com.revolut.service.ProfileService;
import com.revolut.service.ServiceException;
import com.revolut.service.ServiceException.Type;

@Path("profile")
public class Endpoint {

	private MoneyTransferService moneyTransferService;
	
	private ProfileService profileService;

	private static final Logger log = LoggerFactory.getLogger(Endpoint.class);

	public Endpoint(MoneyTransferService moneyTransferService, ProfileService profileService) {
		this.moneyTransferService = moneyTransferService;
		this.profileService = profileService;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("get/{profile}")
	public Response createProfile(
			@PathParam("profile") String profileId			
	) {
		return doOperation(new Callable<JsonPayload>() {

			@Override
			public JsonPayload call() throws Exception {
				
				Optional<Profile> profile = profileService.findProfile(profileId);

				JsonPayload payload;
				
				if(profile.isPresent()) {
					payload = new JsonPayload(true, profile.get());
				} else {
					payload = new JsonPayload(false, 
							new ServiceException("profile does not exist " + profileId, Type.PROFILE_DOES_NOT_EXIST));
				}
				
				return payload;
			}
		});		
			
	}	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("create/{currency}/{amount}")
	public Response createProfile(
			@PathParam("currency") String currencyCode, 
			@PathParam("amount") BigDecimal amount			
			) {
		return doOperation(new Callable<JsonPayload>() {

			@Override
			public JsonPayload call() throws Exception {
				Money money = new Money(Currency.getInstance(currencyCode), amount);
				Profile profile = new Profile(UUID.randomUUID().toString(), money);

				profileService.createProfile(profile);

				JsonPayload payload = new JsonPayload(true, profile);
				
				return payload;
			}
		});		
			
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("transfer/{from}/{to}/{currency}/{amount}")
	public Response transfer(
			@PathParam("from") String profileIdFrom, 
			@PathParam("to") String profileIdTo,
			@PathParam("currency") String currencyCode, 
			@PathParam("amount") BigDecimal amount)
					throws ServiceException {

		return doOperation(new Callable<JsonPayload>() {

			@Override
			public JsonPayload call() throws Exception {
				Money money = new Money(Currency.getInstance(currencyCode), amount);

				MoneyTransfer moneyTransfer = moneyTransferService.transfer(profileIdFrom, profileIdTo, money);

				JsonPayload payload = new JsonPayload(true, moneyTransfer);
				
				return payload;
			}
		});

	}
	
	private Response doOperation(Callable<JsonPayload> operation) {
		try {
			JsonPayload payload = operation.call();
			
			return Response.ok(payload).build();
	
		} catch (ServiceException e) {
	
			log.info(e.getMessage());
			
			JsonPayload payload = new JsonPayload(false, e);
	
			return Response.status(Status.BAD_REQUEST)
					.entity(payload)
					.build();
	
		} catch (Exception e) {
	
			log.error(e.getMessage(), e);
	
			JsonPayload payload = new JsonPayload(false);
			
			return Response.serverError().entity(payload).build();
		}		

	}
}
