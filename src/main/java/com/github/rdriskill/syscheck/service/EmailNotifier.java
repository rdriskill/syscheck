package com.github.rdriskill.syscheck.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

/**
 * @author rdriskill
 */
@Service
public class EmailNotifier {
	private static Logger log = LogManager.getLogger(EmailNotifier.class);
	private MailSender mailSender;
    private SimpleMailMessage templateMessage;
    private Environment env;
    
    public void sendEmail(String text) {
    	SimpleMailMessage msg = new SimpleMailMessage(this.templateMessage);
    	msg.setTo(env.getRequiredProperty("mail.to"));
    	msg.setText(text);
    	
    	try {
    		this.mailSender.send(msg);
    	} catch (MailException ex) {
    		log.error("Error sending email:", ex);
    	}
    }
    
    @Autowired
	public void setMailSender(MailSender mailSender) {
		this.mailSender = mailSender;
	}
	
    @Autowired
	public void setTemplateMessage(SimpleMailMessage templateMessage) {
		this.templateMessage = templateMessage;
	}

    @Autowired
	public void setEnv(Environment env) {
		this.env = env;
	}

}
