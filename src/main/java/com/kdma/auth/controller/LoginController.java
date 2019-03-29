
package com.kdma.auth.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

  /**
   * Return home page.
   * 
   * @return
   */
  @GetMapping("/")
  public String home() {
    return "index";
  }

  /**
   * Return login page or redirect user to profile if already logged in.
   * 
   * @return
   */
  @GetMapping("/login")
  public String login() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (!(auth instanceof AnonymousAuthenticationToken)) {
      return "redirect:/profile";
    }
    return "login";
  }
}
