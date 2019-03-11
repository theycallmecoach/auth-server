package com.kdma.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kdma.auth.model.User;

/**
 * UserRepository interface.  Uses Spring JPA Repository.
 *
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	
	/**
	 * Find a user by email.
	 * 
	 * @param email users email
	 * @return User with the given email address or empty.
	 */
	Optional<User> findByEmail(String email);
	
	/**
	 * Find a user by confirmation token.
	 * 
	 * @param confirmationToken the users confirmation token.
	 * @return User with the given token or empty.
	 */
	Optional<User> findByConfirmationToken(String confirmationToken);
	
	/**
	 * Find a user by id.
	 * 
	 * @param id the users Id.
	 * @return User with the given id or empty.
	 */
	Optional<User> findById(Long id);
}
