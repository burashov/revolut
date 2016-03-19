package com.revolut.endpoint;

import java.net.URI;

import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.revolut.config.ApplicationConfig;
import com.revolut.config.jdbc.JdbcApplicatonConfig;

public class Bootstrap {

	private static final String BASE_URI = "http://localhost:8080/revolut/";

	private static final Logger log = LoggerFactory.getLogger(Bootstrap.class);

	private Bootstrap() {

	}

	public static void main(String[] args) {
		ApplicationConfig config = new JdbcApplicatonConfig();

		config.refresh();

		Endpoint endpoint = new Endpoint(config.getMoneyTransferService(), config.getProfileService());

		ResourceConfig rc = new ResourceConfig()
				.register(endpoint)
				.register(JacksonFeature.class)
				.register(ObjectMapperProvider.class);

		GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);

		log.info("Jersey app started with WADL available at {}application.wadl", BASE_URI);

	}

}
