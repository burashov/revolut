package com.revolut.config;

import com.revolut.config.jdbc.JdbcApplicatonConfig;

public class Bootstrap {

	private Bootstrap() {
		
	}
	
	public static void main(String[] args) {
		ApplicationConfig config = new JdbcApplicatonConfig();
		
		config.refresh();
	}
	
}
