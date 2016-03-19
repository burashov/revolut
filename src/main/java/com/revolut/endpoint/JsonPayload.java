package com.revolut.endpoint;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonInclude(Include.NON_NULL)
public class JsonPayload {

	@JsonProperty
	private boolean ok;

	@JsonProperty
	private Object payload;

	public JsonPayload(boolean ok) {
		this.ok = ok;
	}

	public JsonPayload(boolean ok, Object payload) {
		this.ok = ok;
		this.payload = payload;
	}	
}
