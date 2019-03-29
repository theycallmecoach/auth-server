
package com.kdma.auth.controller;

import java.security.Principal;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.context.MessageSource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.kdma.auth.model.User;
import com.kdma.auth.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * The Class UserController.
 */
@Slf4j
@RestController
public class UserController {

  private static final String DELETE_ACCOUNT = "deleteAccount";

  private static final String DELETE_SUCCESS = "deleteSuccess";

  private static final String PROFILE = "profile";

  private static final String CONFIRMATION_MESSAGE = "confirmationMessage";

  private static final String WARNING_MESSAGE = "warningMessage";

  private final UserRepository repository;

  private final MessageSource messages;

  /**
   * User Controller
   * 
   * @param repository
   * @param messages
   */
  public UserController(UserRepository repository, MessageSource messages) {
    this.repository = repository;
    this.messages = messages;
  }

  /**
   * Return the User
   * 
   * @param principal
   *          contains the security context
   * @return the user
   */
  @GetMapping("/user")
  public User getUser(Principal principal) {
    Optional<User> optUser = repository.findByEmail(principal.getName());
    User user = optUser.orElseThrow(() -> new UsernameNotFoundException("User for principal not found"));
    user.setEmail(Long.toString(user.getId()));
    return user;
  }

  /**
   * Return the deleteAccount page.
   * 
   * @param locale
   * @return
   */
  @GetMapping("/deleteAccount")
  public ModelAndView deleteAccountPage(Locale locale) {
    ModelAndView modelAndView = new ModelAndView(DELETE_ACCOUNT);
    modelAndView.addObject(WARNING_MESSAGE, messages.getMessage("delete.warning", null, locale));
    return modelAndView;
  }

  /**
   * Delete an account.
   * 
   * @param request
   * @param principal
   * @param locale
   * @return
   */
  @PostMapping("/deleteAccount")
  public ModelAndView deleteAccount(HttpServletRequest request, Principal principal, Locale locale) {
    log.debug("User deletion requested for : {}", principal.getName());
    Optional<User> optUser = repository.findByEmail(principal.getName());
    User user = optUser.orElseThrow(() -> new UsernameNotFoundException("User for principal not found"));
    repository.delete(user);
    ModelAndView modelAndView = new ModelAndView("redirect:/" + DELETE_SUCCESS);
    modelAndView.addObject("success", true);
    return modelAndView;
  }

  @GetMapping("/deleteSuccess")
  public ModelAndView deleteSuccessPage(HttpServletRequest request, Locale locale,
                                        @RequestParam(value = "success", required = false) boolean success) {
    if (success) {
      SecurityContextHolder.getContext().setAuthentication(null);
      SecurityContextHolder.clearContext();

      // Invalidate session
      final HttpSession session = request.getSession(false);
      if (session != null) {
        session.invalidate();
      }

      ModelAndView modelAndView = new ModelAndView(DELETE_SUCCESS);
      modelAndView.addObject(CONFIRMATION_MESSAGE, messages.getMessage("delete.success", null, locale));
      return modelAndView;
    }
    return new ModelAndView("redirect:/" + PROFILE);
  }

  @Secured("ROLE_ADMIN")
  @GetMapping("/users")
  public List<User> getAllUsers() {
    log.debug("Accessing list of all users");
    return repository.findAll();
  }
}
