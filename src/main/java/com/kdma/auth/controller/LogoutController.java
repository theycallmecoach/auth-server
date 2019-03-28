package com.kdma.auth.controller;

import java.security.Principal;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.context.MessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.kdma.auth.service.TokenService;

import lombok.extern.slf4j.Slf4j;

/**
 * The Class LogoutController.
 */
@Controller
@Slf4j
public class LogoutController {

	private static final String LOGOUT = "logout";
	private static final String CONFIRMATION_MESSAGE = "confirmationMessage";

	private final TokenService tokenService;
	private MessageSource messages;

	public LogoutController(TokenService tokenService, MessageSource messages) {
		this.tokenService = tokenService;
		this.messages = messages;
	}

	/**
	 * Return logout page.
	 * 
	 * @return
	 */
	@GetMapping("/logout")
	public String logout() {
		return LOGOUT;
	}

	/**
	 * Direct logout.
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/logout")
	public String logout(HttpServletRequest request) {
		log.debug("Direct logout");

		SecurityContextHolder.getContext().setAuthentication(null);
		SecurityContextHolder.clearContext();
		// Invalidate session
		final HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}

		return "redirect:/login?logout";
	}
	
	@PostMapping("/globalLogout")
	public ModelAndView globalLogout(Principal principal, Locale locale) {
		log.debug("Global Logout");
		
		ModelAndView modelAndView = new ModelAndView(LOGOUT);
		
		//Revoke Tokens
		tokenService.revokeTokens(principal.getName());
		modelAndView.addObject(CONFIRMATION_MESSAGE, messages.getMessage("logout.globalConfirmation", null, locale));
		return modelAndView;
	}
}
