package com.revolut.endpoint;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Provider
public class ObjectMapperProvider implements ContextResolver<ObjectMapper> {

	final ObjectMapper defaultObjectMapper;

	public ObjectMapperProvider() {
		defaultObjectMapper = createDefaultMapper();
	}

	@Override
	public ObjectMapper getContext(Class<?> type) {
		return defaultObjectMapper;
	}

	private static ObjectMapper createDefaultMapper() {
		final ObjectMapper result = new ObjectMapper();
		
		result.configure(SerializationFeature.INDENT_OUTPUT, true); // just for this demo
		
		result.setVisibilityChecker(result.getSerializationConfig().getDefaultVisibilityChecker()
			.withCreatorVisibility(Visibility.NONE)
			.withFieldVisibility(Visibility.NONE)
			.withGetterVisibility(Visibility.NONE)
			.withIsGetterVisibility(Visibility.NONE)
			.withSetterVisibility(Visibility.NONE));
		
		return result;
	}

}