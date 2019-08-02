
package com.kdma.auth.service;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * The Class MailContentBuilder.
 */
@Service
public class MailContentBuilder {

  private TemplateEngine templateEngine;

  /**
   * Instantiates a new mail content builder.
   *
   * @param templateEngine
   *          the template engine
   */
  public MailContentBuilder(TemplateEngine templateEngine) {
    this.templateEngine = templateEngine;
  }

  /**
   * Populate email template with given message.
   *
   * @param message
   *          the message
   * @param link
   *          the link
   * @return the string
   */
  public String build(String message, String link) {
    Context context = new Context();
    context.setVariable("message", message);
    context.setVariable("link", link);

    return templateEngine.process("mail/mail", context);
  }
}
