package org.atom.login.controller;

import java.net.URI;
import java.util.Collections;

import javax.validation.Valid;

import org.atom.login.dao.RoleRepository;
import org.atom.login.dao.UserRepository;
import org.atom.login.exception.BadRequestException;
import org.atom.login.exception.GenricException;
import org.atom.login.model.Role;
import org.atom.login.model.RoleName;
import org.atom.login.model.User;
import org.atom.login.model.payload.*;
import org.atom.login.service.AuthenticationService;
import org.atom.login.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;

@RestController
@RequestMapping("/api/auth")
public class SecurityController {

	private static final Logger logger = LoggerFactory.getLogger(SecurityController.class);
	
	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUtil jwtTokenUtil;

	@Autowired
	private AuthenticationService userDetailsService;


	/*@GetMapping({ "/" })
	public String initialPage() {
		return "Initial Load";
	}


	@GetMapping({ "/hello" })
	public String firstPage() {
		return "Hello World";
	}*/

	@PostMapping(value = "/authenticate")
	@ApiImplicitParams(@ApiImplicitParam(name = "Authorization"))
	public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {

		try {
			logger.trace("User Name or Email : "+authenticationRequest.getUsernameOrEmail()+" Password : "+authenticationRequest.getPassword());
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(authenticationRequest.getUsernameOrEmail(), authenticationRequest.getPassword())
					);
		}
		catch (BadCredentialsException e) {
			logger.error(e.getMessage());
			return new ResponseEntity(new GenricResponse(false,"Incorrect username or password"),
					HttpStatus.BAD_REQUEST);
		}


		final UserDetails userDetails = userDetailsService
				.loadUserByUsername(authenticationRequest.getUsernameOrEmail());

		final String jwt = jwtTokenUtil.generateToken(userDetails);
		
		return ResponseEntity.ok(new AuthenticationResponse(jwt));
	}

	@PostMapping("/signup")
	@ApiImplicitParams({})
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {

		// Creating user's account
		User result = null;

		try {
			logger.trace(signUpRequest.toString());
			result = userDetailsService.createUser(signUpRequest);
		}
		catch (BadRequestException e) {
			logger.error(e.getMessage());
			return new ResponseEntity(new GenricResponse(false,e.getMessage()),
					HttpStatus.BAD_REQUEST);
		}

		URI location = ServletUriComponentsBuilder
				.fromCurrentContextPath().path("/api/users/{username}")
				.buildAndExpand(result.getUsername()).toUri();

		return ResponseEntity.created(location).body(new GenricResponse(true, "User registered successfully"));
	}

}
