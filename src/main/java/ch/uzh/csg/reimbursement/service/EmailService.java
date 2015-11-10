package ch.uzh.csg.reimbursement.service;

import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;

import javax.mail.internet.MimeMessage;

import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import ch.uzh.csg.reimbursement.dto.EmailHeaderInfo;
import ch.uzh.csg.reimbursement.model.EmailSendJob;
import ch.uzh.csg.reimbursement.model.NotificationSendJob;
import ch.uzh.csg.reimbursement.model.Role;
import ch.uzh.csg.reimbursement.model.TestEmailSendJob;
import ch.uzh.csg.reimbursement.model.User;

@Service
public class EmailService {

	private static final Logger LOG = LoggerFactory.getLogger(EmailService.class);

	private Set<EmailSendJob> sendJobQueue = new HashSet<EmailSendJob>();

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private VelocityEngine velocityEngine;

	@Value("${mail.defaultEmailTemplatePath}")
	private String defaultEmailTemplatePath;

	@Value("${mail.notificationEmailTemplatePath}")
	private String notificationEmailTemplatePath;

	@Value("${mail.defaultFromEmail}")
	private String defaultFromEmail;

	@Value("${mail.defaultFromName}")
	private String defaultFromName;

	@Value("${mail.defaultSubject}")
	private String defaultSubject;

	public void processSendJob(final EmailSendJob sendJob) {
		MimeMessagePreparator preparator = new MimeMessagePreparator() {
			@Override
			public void prepare(MimeMessage mimeMessage) throws Exception {
				final EmailHeaderInfo headerInfo = sendJob.getHeaderInfo();
				MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
				message.setFrom(headerInfo.getFromEmail(), headerInfo.getFromName());
				message.setTo(headerInfo.getToEmail());
				if (headerInfo.isSetCcEmail()) {
					message.setCc(headerInfo.getCcEmail());
				}
				if (headerInfo.isSetBccEmail()) {
					message.setBcc(headerInfo.getBccEmail());
				}
				if (headerInfo.isSetReplyToEmail()) {
					message.setReplyTo(headerInfo.getReplyToEmail());
				}
				message.setSubject(headerInfo.getSubject());

				Template template = velocityEngine.getTemplate( defaultEmailTemplatePath );
				StringWriter writer = new StringWriter();
				template.merge( sendJob.getContext(), writer );
				String body = writer.toString();
				LOG.error(body);
				message.setText(body, true);
			}
		};
		this.mailSender.send(preparator);
	}

	public void sendEmailPdfSet(User emailRecipient) {
		boolean added = false;
		for(EmailSendJob sendJob : sendJobQueue){
			if(sendJob.getHeaderInfo().getToEmail().equalsIgnoreCase(emailRecipient.getEmail()) && sendJob instanceof NotificationSendJob){
				((NotificationSendJob)sendJob).addPdfItem();
				added = true;
				break;
			}
		}
		if(!added){
			EmailHeaderInfo headerInfo = new EmailHeaderInfo(defaultFromEmail, defaultFromName, emailRecipient.getEmail(), defaultSubject);
			NotificationSendJob notification = new NotificationSendJob(headerInfo, notificationEmailTemplatePath, emailRecipient);
			notification.addPdfItem();
			sendJobQueue.add(notification);
		}
		LOG.info("Email added to the pdf send job: " + emailRecipient.getEmail());
	}

	public void sendEmailExpenseNewAssigned(User emailRecipient) {
		boolean added = false;
		for(EmailSendJob sendJob : sendJobQueue){
			if(sendJob.getHeaderInfo().getToEmail().equalsIgnoreCase(emailRecipient.getEmail()) && sendJob instanceof NotificationSendJob){
				((NotificationSendJob)sendJob).addExpenseItem();
				added = true;
				break;
			}
		}
		if(!added){
			EmailHeaderInfo headerInfo = new EmailHeaderInfo(defaultFromEmail, defaultFromName, emailRecipient.getEmail(), defaultSubject);
			NotificationSendJob notification = new NotificationSendJob(headerInfo, notificationEmailTemplatePath, emailRecipient);
			notification.addExpenseItem();
			sendJobQueue.add(notification);
		}
		LOG.info("Email added the expense send job of:" + emailRecipient.getEmail());
	}

	public void sendOutEmails(){
		//TODO This method should be called all four hours and sends out the emails that are stored in the send queue
		for (EmailSendJob sendJob : sendJobQueue){
			processSendJob(sendJob);
		}
		sendJobQueue.clear();
	}


	public void sendTestEmail() {
		Set<Role> roles = new HashSet<Role>();
		roles.add(Role.PROF);
		User testUser = new User("Christian", "Davatz", "X12344", "davatzc@gmail.com", "Velo Mech", roles);
		EmailHeaderInfo emailHeaderInfo = new EmailHeaderInfo(defaultFromEmail, defaultFromName,testUser.getEmail(), defaultSubject);
		EmailSendJob testJob= new TestEmailSendJob(emailHeaderInfo, defaultEmailTemplatePath);
		sendJobQueue.add(testJob);

		sendEmailExpenseNewAssigned(testUser);
		sendEmailExpenseNewAssigned(testUser);
		sendEmailPdfSet(testUser);

		sendOutEmails();
	}

}