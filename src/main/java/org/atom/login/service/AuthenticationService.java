package org.atom.login.service;

import java.util.ArrayList;
import java.util.Collections;

import org.atom.login.dao.RoleRepository;
import org.atom.login.dao.UserRepository;
import org.atom.login.exception.GenricException;
import org.atom.login.model.Role;
import org.atom.login.model.RoleName;
import org.atom.login.model.User;
import org.atom.login.model.UserPrincipal;
import org.atom.login.model.payload.SignUpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthenticationService implements UserDetailsService{

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
				.orElseThrow(() -> 
				new UsernameNotFoundException("User not found with username or email : " + usernameOrEmail)
						);

		return UserPrincipal.create(user); 
	}

	@Transactional
	public UserDetails loadUserById(Long id) {
		User user = userRepository.findById(id).orElseThrow(
				() -> new UsernameNotFoundException("User not found with id : " + id)
				);

		return UserPrincipal.create(user);
	}

	@Transactional
	private void isUserExistValidation(SignUpRequest signUpRequest) throws GenricException{
		if(userRepository.existsByUsername(signUpRequest.getUsername())) {
			throw new GenricException("Username is already taken!");
		}

		if(userRepository.existsByEmail(signUpRequest.getEmail())) {
			throw new GenricException("Email Address already in use!");
		}
	}

	@Transactional
	public User createUser(SignUpRequest signUpRequest)throws GenricException{
		isUserExistValidation(signUpRequest);
		User user = new User(signUpRequest.getName(), signUpRequest.getUsername(),
				signUpRequest.getEmail(), signUpRequest.getPassword());

		user.setPassword(passwordEncoder.encode(user.getPassword()));

		Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
				.orElseThrow(() -> new GenricException("User Role not set."));

		user.setRoles(Collections.singleton(userRole));
		userRepository.save(user);
		return user;
	}


}
