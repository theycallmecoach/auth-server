
package com.kdma.auth.service;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.MessageSource;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.kdma.auth.AuthProperties;
import com.kdma.auth.model.User;
import com.kdma.auth.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * The Class AccountServiceImpl.
 */

/** The Constant log. */
@Slf4j
@Service
public class AccountServiceImpl implements AccountService {

  /** The user repository. */
  private final UserRepository userRepository;

  /** The email service. */
  private final EmailService emailService;

  /** The password encoder. */
  private final BCryptPasswordEncoder passwordEncoder;

  /** The properties. */
  private final AuthProperties properties;

  /** The messages. */
  private final MessageSource messages;

  /**
   * Instantiates a new account service impl.
   *
   * @param userRepository
   *          the user repository
   * @param emailService
   *          the email service
   * @param passwordEncoder
   *          the password encoder
   * @param properties
   *          the properties
   * @param messages
   *          the messages
   */
  public AccountServiceImpl(UserRepository userRepository, EmailService emailService,
                            BCryptPasswordEncoder passwordEncoder, AuthProperties properties, MessageSource messages) {
    this.userRepository = userRepository;
    this.emailService = emailService;
    this.passwordEncoder = passwordEncoder;
    this.properties = properties;
    this.messages = messages;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.kdma.auth.service.AccountService#registerUser(com.kdma.auth.model.User,
   * java.util.Locale)
   */
  @Override
  public void registerUser(final User user, final Locale locale) {
    log.debug("Register new user...");

    Optional<User> optUser = userRepository.findByEmail(user.getUsername());

    // use existing
    User u = optUser.orElse(user);

    // disable until confirmed via email
    u.setEnabled(false);

    u.setConfirmationToken(UUID.randomUUID().toString());

    userRepository.save(user);

    log.debug("Sending confirmation token to the selected email: {}", user.getEmail());
    log.debug("Sending confirmation token from: {}", properties.getEmailFrom());
    String message = messages.getMessage("email.registration", null, locale);
    String link = properties.getRedirectionUrl() + "/confirmRedirect?token=" + user.getConfirmationToken();
    emailService.prepareAndSend(user.getEmail(), properties.getEmailFrom(), "Registration confirmation", message,
                                link);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.kdma.auth.service.AccountService#confirmUser(java.lang.String, java.lang.String)
   */
  @Override
  public void confirmUser(String token, String password) {
    log.debug("Confirm user with token: {}", token);

    // find the user with the given reset token
    Optional<User> optUser = userRepository.findByConfirmationToken(token);
    User user = optUser.orElseThrow(() -> new UsernameNotFoundException("No user found for token"));

    // set user password
    user.setPassword(passwordEncoder.encode((CharSequence) password));

    // enable the user
    user.setEnabled(true);
    user.setConfirmationToken("");

    userRepository.save(user);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.kdma.auth.service.AccountService#isUserRegistered(com.kdma.auth.model.User)
   */
  @Override
  public boolean isUserRegistered(User user) {
    Optional<User> optUser = userRepository.findByEmail(user.getUsername());
    return optUser.map(u -> u.isEnabled()).orElse(false);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.kdma.auth.service.AccountService#getUserForToken(java.lang.String)
   */
  @Override
  public Optional<User> getUserForToken(String token) {
    return userRepository.findByConfirmationToken(token);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.kdma.auth.service.AccountService#resetPassword(com.kdma.auth.model.User,
   * java.util.Locale)
   */
  @Override
  public void resetPassword(final User user, final Locale locale) {
    log.debug("Resetting password for user: {}", user.getEmail());

    Optional<User> optUser = userRepository.findByEmail(user.getEmail());
    User usr = optUser.orElseThrow(() -> new UsernameNotFoundException("No user found with this email"));

    // Generate random 36-character string token for confirmation link
    usr.setConfirmationToken(UUID.randomUUID().toString());

    // send email with confirmation token
    log.debug("Sending confirmation token to the selected email address: {}", user.getEmail());
    String message = messages.getMessage("email.resetPassword", null, locale);
    String link = properties.getRedirectionUrl() + "/confirmRedirect?token=" + usr.getConfirmationToken();

    emailService.prepareAndSend(usr.getEmail(), properties.getEmailFrom(), "Password reset", message, link);

    // update user entity
    userRepository.save(usr);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.kdma.auth.service.AccountService#changePassword(java.lang.String, java.lang.String,
   * java.lang.String)
   */
  @Override
  public boolean changePassword(String username, String oldPassword, String newPassword) {
    log.debug("Changing password for user: {}", username);
    Optional<User> optUser = userRepository.findByEmail(username);
    if (!optUser.isPresent()) {
      log.error("Cannot find user with this username");
      return false;
    }
    User user = optUser.get();

    boolean passwordMatch = passwordEncoder.matches(oldPassword, user.getPassword());

    log.debug("Current password matches: {}", passwordMatch);
    if (passwordMatch) {
      user.setPassword(passwordEncoder.encode(newPassword));
      userRepository.save(user);
      return true;
    }

    // old password does not match
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.kdma.auth.service.AccountService#changeEmail(java.lang.String, java.lang.String,
   * java.lang.String, java.util.Locale)
   */
  @Override
  public boolean changeEmail(String username, String password, String newEmail, Locale locale) {
    log.debug("Changing e-mail for user: {}", username);

    Optional<User> optionalUser = userRepository.findByEmail(username);
    if (!optionalUser.isPresent()) {
      log.error("Cannot find user with this e-mail!");
      return false;
    }

    User user = optionalUser.get();
    if (userRepository.findByEmail(newEmail).isPresent()) {
      log.warn("User with email {} already exists.", newEmail);

      return false;
    }

    boolean passwordMatch = passwordEncoder.matches(password, user.getPassword());

    log.debug("Current password matches: {}", passwordMatch);
    if (passwordMatch) {
      user.setPendingEmail(newEmail);

      // Generate random 36-character string token for confirmation link
      user.setConfirmationToken(UUID.randomUUID().toString());

      // send email with confirmation token
      log.debug("Sending verification token {} to the selected email: {}", user.getConfirmationToken(), newEmail);

      String message = messages.getMessage("email.verification", null, locale);
      String link = properties.getRedirectionUrl() + "/verifyEmail?token=" + user.getConfirmationToken();

      emailService.prepareAndSend(newEmail, properties.getEmailFrom(), "E-mail change", message, link);

      userRepository.save(user);

      return true;
    }
    // passwords do not match
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.kdma.auth.service.AccountService#verifyEmail(com.kdma.auth.model.User)
   */
  @Override
  public void verifyEmail(User user) {
    log.debug("Verifying e-mail {}", user.getPendingEmail());

    // Set new e-mail
    user.setEmail(user.getPendingEmail());
    user.setPendingEmail(null);
    user.setConfirmationToken("");

    // Save user
    userRepository.save(user);
  }

}
