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
import ch.uzh.csg.reimbursement.dto.ExpenseCountsDto;
import ch.uzh.csg.reimbursement.model.EmailReceiver;
import ch.uzh.csg.reimbursement.model.EmailSendJob;
import ch.uzh.csg.reimbursement.model.Expense;
import ch.uzh.csg.reimbursement.model.ExpenseState;
import ch.uzh.csg.reimbursement.model.NotificationSendJob;
import ch.uzh.csg.reimbursement.model.Role;
import ch.uzh.csg.reimbursement.model.User;
import ch.uzh.csg.reimbursement.repository.EmailReceiverRepositoryProvider;
import ch.uzh.csg.reimbursement.repository.ExpenseRepositoryProvider;
import ch.uzh.csg.reimbursement.repository.UserRepositoryProvider;

@Service
public class EmailService {

	private static final Logger LOG = LoggerFactory.getLogger(EmailService.class);

	//	private Set<EmailSendJob> sendJobQueue = new HashSet<EmailSendJob>();

	@Autowired
	private ExpenseRepositoryProvider expenseRepoProvider;

	@Autowired
	private EmailReceiverRepositoryProvider emailReceiverProvider;

	@Autowired
	private UserRepositoryProvider userProvider;

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

				Template template = velocityEngine.getTemplate( sendJob.getTemplatePath() );
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
		//		boolean added = false;
		//		for(EmailSendJob sendJob : sendJobQueue){
		//			if(sendJob.getHeaderInfo().getToEmail().equalsIgnoreCase(emailRecipient.getEmail()) && sendJob instanceof NotificationSendJob){
		//				((NotificationSendJob)sendJob).increasePdfItemCounter();
		//				added = true;
		//				break;
		//			}
		//		}
		//		if(!added){
		//			EmailHeaderInfo headerInfo = new EmailHeaderInfo(defaultFromEmail, defaultFromName, emailRecipient.getEmail(), defaultSubject);
		//			NotificationSendJob notification = new NotificationSendJob(headerInfo, notificationEmailTemplatePath, emailRecipient);
		//			notification.increasePdfItemCounter();
		//			sendJobQueue.add(notification);
		//		}
		if(!emailReceiverProvider.contains(emailRecipient.getUid())){
			emailReceiverProvider.create(new EmailReceiver(emailRecipient.getUid()));
			LOG.info("User added to the EmailReceiver's list - pdf has to be signed by:"+emailRecipient.getFirstName()+" "+emailRecipient.getLastName()+" roles: "+ emailRecipient.getRoles());
		}else{
			LOG.info("User not added to the list, already present: " + emailRecipient.getRoles());
		}
	}

	public void sendEmailExpenseNewAssigned(User emailRecipient) {
		//		boolean added = false;
		//		for(EmailSendJob sendJob : sendJobQueue){
		//			if(sendJob.getHeaderInfo().getToEmail().equalsIgnoreCase(emailRecipient.getEmail()) && sendJob instanceof NotificationSendJob){
		//				((NotificationSendJob)sendJob).increaseExpenseItemCounter();
		//				added = true;
		//				break;
		//			}
		//		}
		//		if(!added){
		//			EmailHeaderInfo headerInfo = new EmailHeaderInfo(defaultFromEmail, defaultFromName, emailRecipient.getEmail(), defaultSubject);
		//			NotificationSendJob notification = new NotificationSendJob(headerInfo, notificationEmailTemplatePath, emailRecipient);
		//			notification.increaseExpenseItemCounter();
		//			sendJobQueue.add(notification);
		//		}
		if(!emailReceiverProvider.contains(emailRecipient.getUid())){
			emailReceiverProvider.create(new EmailReceiver(emailRecipient.getUid()));
			LOG.info("User added to the EmailReceiver's list - expenses has to be checked by:"+emailRecipient.getFirstName()+" "+emailRecipient.getLastName()+"roles: "+ emailRecipient.getRoles());
		}else{
			LOG.info("Email not added the expense send job of:" + emailRecipient.getEmail());
		}
	}

	//	TODO @Scheduled(cron="${mail.sendOutEmailsCron}")
	public void sendOutEmails(){
		//		for (EmailSendJob sendJob : sendJobQueue){
		//			processSendJob(sendJob);
		//		}
		//		sendJobQueue.clear();

		for(EmailReceiver emailReceiver : emailReceiverProvider.findAll()){
			User user = userProvider.findByUid(emailReceiver.getUid());
			ExpenseCountsDto counts = getCountsForUser(user);
			EmailHeaderInfo headerInfo = new EmailHeaderInfo(defaultFromEmail, defaultFromName, user.getEmail(), defaultSubject);
			NotificationSendJob notification = new NotificationSendJob(headerInfo, notificationEmailTemplatePath, user,counts.getNumberOfExpensesToCheck(),counts.getNumberOfPdfsToSign());
			//			processSendJob(notification);
			LOG.info("Sent to:" + user.getFirstName());
			LOG.info("number of expenseItemsToCheck:"+counts.getNumberOfExpensesToCheck());
			LOG.info("number of pdfsToSign:"+counts.getNumberOfPdfsToSign());
			//TODO clean the list after successfull sending
		}
	}


	public void sendTestEmail() {

		//creation of test Email
		//		Set<Role> roles = new HashSet<Role>();
		//		roles.add(Role.PROF);
		//		User testUser = new User("Christian", "Davatz", "X12344", "davatzc@gmail.com", "Velo Mech", roles);
		//		EmailHeaderInfo emailHeaderInfo = new EmailHeaderInfo(defaultFromEmail, defaultFromName,testUser.getEmail(), defaultSubject);
		//		EmailSendJob testJob= new TestEmailSendJob(emailHeaderInfo, defaultEmailTemplatePath);
		//		sendJobQueue.add(testJob);

		//		sendEmailExpenseNewAssigned(testUser);
		//		sendEmailExpenseNewAssigned(testUser);
		//		sendEmailPdfSet(testUser);
		sendOutEmails();
	}

	//TODO this method needs heavy improvement!!
	private ExpenseCountsDto getCountsForUser(User user){
		if(user.getRoles().contains(Role.FINANCE_ADMIN)){
			//finance Admin Checks
			Set<Expense> expensesAssignedToFinanceAdmin = expenseRepoProvider.findAllByFinanceAdmin(user);
			Set<Expense> expensesAssignedToFinanceAdminStateToSign = new HashSet<Expense>();
			Set<Expense> expensesAssignedToFinanceAdminStateToCheck = new HashSet<Expense>();
			for(Expense expense : expensesAssignedToFinanceAdmin){
				if(expense.getState().equals(ExpenseState.TO_SIGN_BY_FINANCE_ADMIN)){
					expensesAssignedToFinanceAdminStateToSign.add(expense);
				}else if(expense.getState().equals(ExpenseState.ASSIGNED_TO_FINANCE_ADMIN)){
					expensesAssignedToFinanceAdminStateToCheck.add(expense);
				}
			}
			return new ExpenseCountsDto(expensesAssignedToFinanceAdminStateToCheck.size(),expensesAssignedToFinanceAdminStateToSign.size());
		}else if(user.getRoles().contains(Role.PROF)){
			//Manager Checks
			Set<Expense> expensesAssignedToManager = expenseRepoProvider.findAllByAssignedManager(user);
			Set<Expense> expensesAssignedToManagerStateToSign = new HashSet<Expense>();
			Set<Expense> expensesAssignedToManagerStateToCheck = new HashSet<Expense>();
			for(Expense expense : expensesAssignedToManager){
				if(expense.getState().equals(ExpenseState.TO_SIGN_BY_MANAGER)){
					expensesAssignedToManagerStateToSign.add(expense);
				}else if(expense.getState().equals(ExpenseState.ASSIGNED_TO_MANAGER)){
					expensesAssignedToManagerStateToCheck.add(expense);
				}
			}
			return new ExpenseCountsDto(expensesAssignedToManagerStateToCheck.size(),expensesAssignedToManagerStateToSign.size());
		}
		else{
			return new ExpenseCountsDto(0, expenseRepoProvider.findAllByStateForUser(ExpenseState.TO_SIGN_BY_USER, user).size());
		}
	}
}