package com.smart.services;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

	public boolean sendEmail(String subject, String message, String to) {
		String from = "vivek.dama2000@gmail.com";
		boolean flag = false;
		// variable for gmail host
		String host = "smtp.gmail.com";

		//  get the system properties
		Properties properties = System.getProperties();
		System.out.println(properties);

		// setting important information in the properties object
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");

		/* Step:1 Get Session object */
		Session session = Session.getInstance(properties, new Authenticator() {

			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				// TODO Auto-generated method stub
				return new PasswordAuthentication("vivek.dama2000@gmail.com", "exmq abzc ncvt bryc");
			}

		});
		session.setDebug(true);

		// Step2: Compose the message
		MimeMessage mimeMessage = new MimeMessage(session);
		try {
			// from email
			mimeMessage.setFrom(from);

			// adding recipient
			mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

			// adding subject to message
			mimeMessage.setSubject(subject);

			// Adding message
			//mimeMessage.setText(message);
			mimeMessage.setContent(message, "text/html");

			// Step3: Send message using transport class
			Transport.send(mimeMessage);
			flag = true;
			System.out.println("Email sent sucessfully");
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return flag;
	}
}
