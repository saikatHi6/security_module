package org.atom.login.model.payload;

import java.io.Serializable;

public class AuthenticationResponse implements Serializable {
	private final String jwt;
	private String tokenType = "Bearer";
	public AuthenticationResponse(String jwt) {
		this.jwt = jwt;
	}

	public String getJwt() {
		return jwt;
	}

	public String getTokenType() {
		return tokenType;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}
}
