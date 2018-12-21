package com.github.rdriskill.syscheck;

import java.util.Properties;

import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * @author rdriskill
 */
@Configuration
public class MailConfig implements EnvironmentAware {

	private Environment env;

	@Bean
	public JavaMailSender mailSender() {
		Properties props = new Properties();
		props.put("mail.debug", env.getRequiredProperty("mail.debug"));
		props.put("mail.smtp.auth", env.getRequiredProperty("mail.auth"));
		props.put("mail.smtp.starttls.enable", env.getRequiredProperty("mail.starttls"));
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.socketFactory.fallback", "false");
		
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setJavaMailProperties(props);
		mailSender.setHost(env.getRequiredProperty("mail.host"));
		mailSender.setPort(Integer.valueOf(env.getRequiredProperty("mail.port")));
		mailSender.setProtocol(env.getRequiredProperty("mail.protocol"));
		mailSender.setUsername(env.getRequiredProperty("mail.user"));
		mailSender.setPassword(env.getRequiredProperty("mail.pass"));
		mailSender.setJavaMailProperties(props);
		
		return mailSender;
	}

	@Bean
	public SimpleMailMessage simpleMailMessage() {
		String from = env.getRequiredProperty("mail.from");
		String subject = env.getRequiredProperty("mail.subject");
		SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
		simpleMailMessage.setFrom(from);
		simpleMailMessage.setSubject(subject);
		return simpleMailMessage;
	}

	@Override
	public void setEnvironment(Environment env) {
		this.env = env;
	}

}
