
package com.kdma.auth.service;

import java.util.Locale;
import java.util.Optional;

import com.kdma.auth.model.User;

/**
 * The Interface AccountService.
 */
public interface AccountService {

  /**
   * Register user.
   *
   * @param user
   *          the user
   * @param locale
   *          the locale
   */
  void registerUser(User user, Locale locale);

  /**
   * Confirm user.
   *
   * @param token
   *          the token
   * @param password
   *          the password
   */
  void confirmUser(String token, String password);

  /**
   * Checks if is user registered.
   *
   * @param user
   *          the user
   * @return true, if is user registered
   */
  boolean isUserRegistered(User user);

  /**
   * Gets the user for token.
   *
   * @param token
   *          the token
   * @return the user for token
   */
  Optional<User> getUserForToken(String token);

  /**
   * Reset password.
   *
   * @param user
   *          the user
   * @param locale
   *          the locale
   */
  void resetPassword(User user, Locale locale);

  /**
   * Change password.
   *
   * @param username
   *          the username
   * @param oldPassword
   *          the old password
   * @param newPassword
   *          the new password
   * @return true, if successful
   */
  boolean changePassword(String username, String oldPassword, String newPassword);

  /**
   * Change email.
   *
   * @param username
   *          the username
   * @param password
   *          the password
   * @param newEmail
   *          the new email
   * @param locale
   *          the locale
   * @return true, if successful
   */
  boolean changeEmail(String username, String password, String newEmail, Locale locale);

  /**
   * Verify email.
   *
   * @param user
   *          the user
   */
  void verifyEmail(User user);

}
