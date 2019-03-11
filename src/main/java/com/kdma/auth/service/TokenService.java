package com.kdma.auth.service;

public interface TokenService {
	/**
	 * Revoke access and refresh tokens for user with given username.
	 * 
	 * @param username
	 */
	public void revokeTokens(String username);
}
