package ch.uzh.csg.reimbursement.mail;

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
			@Override
			public void prepare(MimeMessage mimeMessage) throws Exception {
				MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
				//				message.setTo("sebschrepfer@hotmail.com");
				//				message.setCc("davatzc@gmail.com");
				message.setTo("crixx@davatz.eu");
				message.setFrom("bocek@ifi.uzh.ch");
				message.setSubject("[reimbursement] New expense item");

				Map<String, Object> model = new HashMap<String, Object>();
				model.put("firstname", "Sebastian");
				model.put("lastname", "Schrepfer");

				String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "ch/uzh/csg/reimbursement/mail/email-template.vm", "utf-8", model);
				message.setText(text, true);
			}
		};
		this.mailSender.send(preparator);
	}

}