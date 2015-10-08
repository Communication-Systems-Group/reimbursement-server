package ch.uzh.csg.reimbursement.configuration;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfiguration {

	@Value("${email.host}")
	private String host;

	@Bean
	public JavaMailSender javaMailService() {
		JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setProtocol("smtp");
        sender.setHost("merlin.metanet.ch");
        sender.setPort(25);
        sender.setUsername("reimbursement@davatz.eu");
        sender.setPassword("pnDe978#");

        Properties mailProps = new Properties();
        mailProps.put("mail.smtps.auth", "true");
        mailProps.put("mail.smtp.starttls.enable", "true");
        mailProps.put("mail.smtp.debug", "true");

        sender.setJavaMailProperties(mailProps);
        return sender;
	}

	@Bean
	public SimpleMailMessage simpleMailMessage() {
		SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
		simpleMailMessage.setFrom("davatzc@gmail.com");
		simpleMailMessage.setSubject("[reimbursement] Notification");
		simpleMailMessage.setText("Empty email");
		return simpleMailMessage;
	}
	

//	private Properties getMailProperties() {
//		Properties properties = new Properties();
//		properties.setProperty("mail.transport.protocol", "smtp");//smtps
//		properties.setProperty("mail.smtp.auth", "true");
//		properties.setProperty("mail.smtp.starttls.enable", "true");
//		properties.setProperty("mail.smtps.ssl.trust", "true");
//		properties.setProperty("mail.smtp.debug", "true");
//		return properties;
//	}
}