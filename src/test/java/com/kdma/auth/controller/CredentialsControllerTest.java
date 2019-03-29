
package com.kdma.auth.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.kdma.auth.AuthProperties;
import com.kdma.auth.service.AccountService;
import com.kdma.auth.service.TokenService;

@RunWith(SpringRunner.class)
@WebMvcTest(value = CredentialsController.class, secure = false)
public class CredentialsControllerTest {

  @Autowired
  private MockMvc mvc;

  @MockBean
  private AccountService registrationService;

  @MockBean
  private AuthProperties properties;

  @MockBean
  private TokenService tokenService;

  @Test
  public void testGivenForgottenPasswordEndpointWhenAccessingForgottonPageThenVerifyForgottonViewIsReturned()
      throws Exception {
    this.mvc.perform(get("/forgotten")).andExpect(status().isOk()).andExpect(view().name("forgotten"));
  }

  @Test
  public void testGivenChangePasswordEndpointWhenAccessingForgottenPageThenVerifyChangePasswordReturned()
      throws Exception {
    // @formatter:off
		this.mvc.perform(get("/changePassword")).andExpect(status().isOk()).andExpect(view().name("changePassword"));
		// @formatter:on
  }

}
