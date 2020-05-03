package org.atom.login.model.payload;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

public class AuthenticationRequest implements Serializable {
	
	@NotBlank
	private String usernameOrEmail;
	
	@NotBlank
	private String password;

	
	public String getUsername() {
		return usernameOrEmail;
	}

	public void setUsername(String username) {
		this.usernameOrEmail = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	//need default constructor for JSON Parsing
	public AuthenticationRequest()
	{

	}

	public AuthenticationRequest(String username, String password) {
		this.setUsername(username);
		this.setPassword(password);
	}
}
