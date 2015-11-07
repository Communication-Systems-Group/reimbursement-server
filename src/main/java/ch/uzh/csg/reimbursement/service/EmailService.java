package ch.uzh.csg.reimbursement.service;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;

import ch.uzh.csg.reimbursement.dto.EmailConvenienceClass;
import ch.uzh.csg.reimbursement.model.User;

@Service
public class EmailService {

	private static final Logger LOG = LoggerFactory.getLogger(EmailService.class);

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private VelocityEngine velocityEngine;

	@Value("${mail.defaultEmailTemplatePath}")
	private String defaultEmailTemplatePath;

	public void sendEmail(final EmailConvenienceClass emailCC) {
		MimeMessagePreparator preparator = new MimeMessagePreparator() {
			@Override
			public void prepare(MimeMessage mimeMessage) throws Exception {
				MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
				message.setFrom(emailCC.getFromEmail(), emailCC.getFromName());
				message.setTo(emailCC.getToEmail());
				if (emailCC.isSetCcEmail()) {
					message.setCc(emailCC.getCcEmail());
				}
				if (emailCC.isSetBccEmail()) {
					message.setBcc(emailCC.getBccEmail());
				}
				if (emailCC.isSetReplyToEmail()) {
					message.setReplyTo(emailCC.getReplyToEmail());
				}
				message.setSubject(emailCC.getSubject());

				String text;
				if(emailCC.isSetTemplatePath() && !emailCC.isSetMessageModel()){
					text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine,emailCC.getTemplatePath(), "utf-8", null);
				} else if(emailCC.isSetTemplatePath() && emailCC.isSetMessageModel()){
					text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine,emailCC.getTemplatePath(), "utf-8", emailCC.getMessageModel());
				}else{
					text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine,defaultEmailTemplatePath, "utf-8", null);
				}
				message.setText(text, true);
			}
		};
		this.mailSender.send(preparator);
	}

	public void sendEmailPdfSet(User emailRecipient) {
		LOG.info("Email regarding pdf set sent to:" + emailRecipient.getEmail());
	}

	public void sendEmailExpenseNewAssigned(User emailRecipient) {
		LOG.info("Email sent to:" + emailRecipient.getEmail());
	}

	public void sendTestEmail() {

		/*  next, get the Template  */
		Template t = velocityEngine.getTemplate( defaultEmailTemplatePath );
		/*  create a context and add data */
		VelocityContext context = new VelocityContext();

		/* create our list of links  */
		ArrayList headerLinkList = new ArrayList();
		Map map = new HashMap();
		map.put("address", "/login");
		map.put("text", "Login");
		headerLinkList.add( map );

		map = new HashMap();
		map.put("address", "/settings");
		map.put("text", "Settings");
		headerLinkList.add( map );

		map = new HashMap();
		map.put("address", "/user");
		map.put("text", "User");
		headerLinkList.add( map );


		//		Map<String, Object> model = new HashMap<String, Object>();
		//		model.put("firstname", firstname);
		//		model.put("lastname", lastname);
		//		EmailConvenienceClass email = new EmailConvenienceClass("christian.davatz@uzh.ch", "Christian Davatz", "davatzc@gmail.com", "testmail", defaultEmailTemplatePath, list);

		context.put("headerLinkList", headerLinkList);
		context.put("greeting", "Hello John");
		context.put("lead", "Phasellus dictum sapien a neque luctus cursus. Pellentesque sem dolor, fringilla et pharetra vitae.");
		context.put("message", "Phasellus dictum sapien a neque luctus cursus. Pellentesque sem dolor, fringilla et pharetra vitae. consequat vel lacus. Sed iaculis pulvinar ligula, ornare fringilla ante viverra et. ");
		context.put("callout", "Phasellus dictum sapien a neque luctus cursus. Pellentesque sem dolor, fringilla et pharetra vitae.");

		Map calloutLink = new HashMap();
		calloutLink.put("address", "/calloutLink");
		calloutLink.put("text", "CalloutLinkText");
		context.put("calloutLink", calloutLink);

		/* create our list of links  */
		ArrayList footerLinkList = new ArrayList();
		map = new HashMap();
		map.put("address", "/login");
		map.put("text", "Login");
		footerLinkList.add( map );

		map = new HashMap();
		map.put("address", "/settings");
		map.put("text", "Settings");
		footerLinkList.add( map );

		map = new HashMap();
		map.put("address", "/user");
		map.put("text", "User");
		footerLinkList.add( map );

		context.put("footerLinkList", footerLinkList);

		Map lastFooterLink = new HashMap();
		lastFooterLink.put("address", "/Last");
		lastFooterLink.put("text", "Last");
		context.put("lastFooterLink", lastFooterLink);

		/* now render the template into a StringWriter */
		StringWriter writer = new StringWriter();
		t.merge( context, writer );
		/* show the World */
		System.out.println( writer.toString() );

	}

}