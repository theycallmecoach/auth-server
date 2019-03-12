package com.kdma.auth.service;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.kdma.auth.model.User;
import com.kdma.auth.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * The Class UserService.
 */
@Slf4j
@Service
public class UserService implements UserDetailsService {

	/** The user repository. */
	private final UserRepository userRepository;
	
	
	
	/**
	 * Instantiates a new user service.
	 *
	 * @param userRepository the user repository
	 */
	public UserService(UserRepository userRepository) {
		super();
		this.userRepository = userRepository;
	}



	/* (non-Javadoc)
	 * @see org.springframework.security.core.userdetails.UserDetailsService#loadUserByUsername(java.lang.String)
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.debug("Loading user details for username: {}", username);
		Optional<User> optUser = userRepository.findByEmail(username);
		User user = optUser.orElseThrow(() -> new UsernameNotFoundException("Email address not found."));
		return user;
	}

}
