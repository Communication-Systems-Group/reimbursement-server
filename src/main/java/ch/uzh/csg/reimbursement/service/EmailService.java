package ch.uzh.csg.reimbursement.service;

import java.util.HashMap;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;

import ch.uzh.csg.reimbursement.dto.Templateinformation;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private VelocityEngine velocityEngine;
	
	private String noAddress = "noAddress";
	
	//TODO check if Emailvalidation is needed!
	public void sendEmail(final String fromEmail, final String fromName, final String to, final String subject, Templateinformation templateInfo) {
		sendEmail(fromEmail, fromName,to, subject, noAddress, noAddress, noAddress, templateInfo);
	}
	
	public void sendEmail(final String fromEmail,final String fromName, final String toEmail, final String subject, final String ccEmail, final String bccEmail, final String replyToEmail,final Templateinformation templateInfo) {
		MimeMessagePreparator preparator = new MimeMessagePreparator() {
			@Override
			public void prepare(MimeMessage mimeMessage) throws Exception {
				MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
				message.setFrom(fromEmail, fromName);
				message.setTo(toEmail);				
				if (!ccEmail.equals(noAddress)){
					message.setCc(ccEmail);		
				}
				if (!bccEmail.equals(noAddress)){
					message.setBcc(bccEmail);				
								}
				if (!replyToEmail.equals(noAddress)){
					message.setReplyTo(replyToEmail);
				}
				message.setSubject(subject);

				String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, templateInfo.getTemplatePath(), "utf-8", templateInfo.getModel());
				message.setText(text, true);
			}
		};
		this.mailSender.send(preparator);
	}

}