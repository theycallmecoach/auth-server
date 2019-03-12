package com.kdma.auth.controller;

import java.util.Locale;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.kdma.auth.model.User;
import com.kdma.auth.service.AccountService;

import lombok.extern.slf4j.Slf4j;

/**
 * The Class RegisterController.
 */
@Slf4j
@Controller
public class RegisterController {
	private static final String CONFIRM = "confirm";
	private static final String REGISTER = "register";

	private static final String SUCCESS_MESSAGE = "successMessage";
	private static final String CONFIRMATION_MESSAGE = "confirmationMessage";
	private static final String ERROR_MESSAGE = "errorMessage";

	private final AccountService accountService;
	private final MessageSource messages;

	public RegisterController(AccountService accountService, MessageSource messages) {
		super();
		this.accountService = accountService;
		this.messages = messages;
	}

	/**
	 * <p>
	 * Return ModelAndView for registration page.
	 * </p>
	 */
	@GetMapping("/register")
	public ModelAndView showRegistrationPage(ModelAndView modelAndView, User user) {
		modelAndView.addObject("user", user);
		modelAndView.setViewName(REGISTER);
		return modelAndView;
	}

	@PostMapping("/register")
	public ModelAndView processRegistrationForm(ModelAndView modelAndView, @Valid User user, Locale locale) {
		log.debug("User registration - POST");

		modelAndView.setViewName(REGISTER);

		if (accountService.isUserRegistered(user)) {
			log.warn("This user already exists: {}", user);

			modelAndView.addObject(ERROR_MESSAGE,
					messages.getMessage("registration.emailExists", new Object[] { user.getEmail() }, locale));
			return modelAndView;
		}
		accountService.registerUser(user, locale);
		modelAndView.addObject(CONFIRMATION_MESSAGE,
				messages.getMessage("registration.confirmationEmail", new Object[] { user.getEmail() }, locale));
		return modelAndView;
	}

	@GetMapping("/confirm")
	public ModelAndView showConfirmationPage(final ModelAndView modelAndView, @RequestParam("token") String token,
			@RequestParam(value = "mobile", required = false, defaultValue = "false") boolean mobile,
			@RequestParam(value = "passwordError", required = false) boolean passwordError, Locale locale) {
		log.debug("GET -> show Confirmation");

		modelAndView.setViewName("CONFIRM");
		if (passwordError) {
			log.debug("Passwords are not matching!");
			modelAndView.addObject(ERROR_MESSAGE, messages.getMessage("password.notMatching", null, locale));
		}

		Optional<User> optUser = accountService.getUserForToken(token);
		optUser.ifPresentOrElse(user -> modelAndView.addObject("confirmationToken", user.getConfirmationToken()),
				() -> {
					log.debug("No user found that matches this token: {}", token);
					modelAndView.addObject("invalidToken",
							messages.getMessage("registration.invalidToken", null, locale));
				});
		modelAndView.addObject("mobile", mobile);
		return modelAndView;
	}

	@GetMapping("/confirmRedirect")
	public String showRedirectionFonfirmationPage(@RequestParam("token") String token) {
		return "redirect:/confirm?token=" + token;
	}

	@PostMapping("/confirm")
	public ModelAndView processConfirmationForm(ModelAndView modelAndView, @RequestParam("token") String token,
			@RequestParam("password") String password, @RequestParam("confirmaPassword") String confirmPassword,
			Locale locale) {
		log.debug("POST -> confirmation form");

		modelAndView.setViewName(CONFIRM);

		if (!password.contentEquals(confirmPassword)) {
			modelAndView.setView(new RedirectView(CONFIRM));
			modelAndView.addObject("token", token);
			modelAndView.addObject("passwordError", true);

			return modelAndView;
		}

		modelAndView.addObject("loginLink", "/login");
		accountService.confirmUser(token, password);
		modelAndView.addObject(SUCCESS_MESSAGE, messages.getMessage("registration.passwordSuccess", null, locale));
		return modelAndView;
	}
}
