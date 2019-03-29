
package com.kdma.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// TODO: Auto-generated Javadoc
/**
 * The Class ProfileController.
 */
@Controller
public class ProfileController {

  /**
   * Return Profile page.
   *
   * @return the string
   */
  @GetMapping("/profile")
  public String profile() {
    return "profile";
  }
}
