package ch.uzh.csg.reimbursement.mail;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.mail.internet.MimeMessage;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;

import ch.uzh.csg.reimbursement.model.Role;
import ch.uzh.csg.reimbursement.model.User;

@Service
public class SimpleEmailService {

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private VelocityEngine velocityEngine;

	public void setMailSender(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	public void setVelocityEngine(VelocityEngine velocityEngine) {
		this.velocityEngine = velocityEngine;
	}

	public void sendEmail() {
		sendConfirmationEmail();
	}

	private void sendConfirmationEmail() {
		MimeMessagePreparator preparator = new MimeMessagePreparator() {
			public void prepare(MimeMessage mimeMessage) throws Exception {
				MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
				message.setTo("sebschrepfer@hotmail.com");
				message.setCc("davatzc@gmail.com");
				message.setFrom("christian.davatz@uzh.ch");
				message.setSubject("[reimbursement] New expense item");
				
				Map<String, Object> model = new HashMap<String, Object>();
				model.put("firstname", "Sebastian");
				model.put("lastname", "Schrepfer");
				
				String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine,
						"ch/uzh/csg/reimbursement/mail/email-template.vm", model);
				message.setText(text, true);
			}
		};
		this.mailSender.send(preparator);
	}

}