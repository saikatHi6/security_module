package org.atom.login.controller;

import java.util.List;

import org.atom.login.annotation.CurrentUser;
import org.atom.login.dao.UserRepository;
import org.atom.login.exception.GenricException;
import org.atom.login.exception.ResourceNotFoundException;
import org.atom.login.model.User;
import org.atom.login.model.UserPrincipal;
import org.atom.login.model.payload.GenricResponse;
import org.atom.login.model.payload.UserIdentityAvailability;
import org.atom.login.model.payload.UserProfile;
import org.atom.login.model.payload.UserSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserController {

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserRepository userRepository;

	@GetMapping("/user/me")
	@PreAuthorize("hasRole('USER')")
	public UserSummary getCurrentUser(@CurrentUser UserPrincipal currentUser) {
		UserSummary userSummary = new UserSummary(currentUser.getId(), currentUser.getUsername(), currentUser.getName(),currentUser.getEmail());
		logger.trace(userSummary.toString());
		return userSummary;
	}

	@GetMapping("/user/checkUsernameAvailability")
	public UserIdentityAvailability checkUsernameAvailability(@RequestParam(value = "username") String username) {
		Boolean isAvailable = !userRepository.existsByUsername(username);
		logger.trace(username);
		return new UserIdentityAvailability(isAvailable);
	}

	@GetMapping("/user/checkEmailAvailability")
	public UserIdentityAvailability checkEmailAvailability(@RequestParam(value = "email") String email) {
		Boolean isAvailable = !userRepository.existsByEmail(email);
		logger.trace(email);
		return new UserIdentityAvailability(isAvailable);
	}

	@GetMapping("/users/{username}")
	public UserProfile getUserProfile(@PathVariable(value = "username") String username) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
		
		logger.trace(user.toString());
		UserProfile userProfile = new UserProfile(user.getId(), user.getUsername(), user.getName(), user.getCreatedAt());

		return userProfile;
	}

	@GetMapping("/users")
	public ResponseEntity<?> getAllListOfUsers() {
		List<User> users = null;
		
		try{
			users = userRepository.findAll();
			logger.trace("Total number of users: "+users.size());
		}
		catch (GenricException e) {
			logger.error(e.getMessage());
			return new ResponseEntity<>(new GenricException(e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return new ResponseEntity<>(users, HttpStatus.OK);
	}

}
