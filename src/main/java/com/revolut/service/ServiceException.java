package com.revolut.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

public class ServiceException extends Exception {

	private static final long serialVersionUID = 1L;

	@JsonProperty
	private Type type;
	
	@JsonProperty
	private String message;
	
	public ServiceException(String message, Type type) {
		super(message);
		
		Preconditions.checkNotNull(type);
		
		this.message = message;
		this.type = type;
	}
	
	public Type getType() {
		return type;
	}
	
	public enum Type {
		PROFILE_AND_MONEY_MUST_BE_IN_THE_SAME_CURRENCY,
		PROFILE_DOES_NOT_HAVE_ENOUGH_MONEY,
		PROFILE_DOES_NOT_EXIST
	}
}
