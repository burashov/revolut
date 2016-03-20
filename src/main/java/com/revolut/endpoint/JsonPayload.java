package com.revolut.endpoint;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.common.base.MoreObjects;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonInclude(Include.NON_NULL)
public class JsonPayload {

	@JsonProperty
	private boolean ok;

	@JsonProperty
	private Object payload;

	JsonPayload() { 		
	}
	
	public JsonPayload(boolean ok) {
		this.ok = ok;
	}

	public JsonPayload(boolean ok, Object payload) {
		this.ok = ok;
		this.payload = payload;
	}
	
	public Object getPayload() {
		return payload;
	}
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("ok", ok)
				.add("payload", payload)
				.toString();
	}

	public boolean isOk() {
		return ok;
	}
}
