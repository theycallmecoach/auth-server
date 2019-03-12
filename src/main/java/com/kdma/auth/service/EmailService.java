package com.kdma.auth.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailService {
	private final JavaMailSender mailSender;
	private final MailContentBuilder mailContentBuilder;

	public EmailService(JavaMailSender mailSender, MailContentBuilder mailContentBuilder) {
		super();
		this.mailSender = mailSender;
		this.mailContentBuilder = mailContentBuilder;
	}

	@Async
	public void prepareAndSend(String to, String from, String subject, String message, String link) {
		MimeMessagePreparator messagePreparator = mimeMessage -> {
			MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
			messageHelper.setFrom(from);
			messageHelper.setTo(to);
			messageHelper.setSubject(subject);

			String content = mailContentBuilder.build(message, link);
			messageHelper.setText(content, true);
		};

		log.debug("Sending mail....");
		mailSender.send(messagePreparator);
	}

}
