package ch.uzh.csg.reimbursement.service;

import static ch.uzh.csg.reimbursement.model.ExpenseState.ASSIGNED_TO_FINANCE_ADMIN;
import static ch.uzh.csg.reimbursement.model.ExpenseState.ASSIGNED_TO_MANAGER;
import static ch.uzh.csg.reimbursement.model.ExpenseState.SIGNED;
import static ch.uzh.csg.reimbursement.model.ExpenseState.TO_BE_ASSIGNED;
import static ch.uzh.csg.reimbursement.model.ExpenseState.TO_SIGN_BY_FINANCE_ADMIN;
import static ch.uzh.csg.reimbursement.model.ExpenseState.TO_SIGN_BY_MANAGER;
import static ch.uzh.csg.reimbursement.model.ExpenseState.TO_SIGN_BY_USER;
import static ch.uzh.csg.reimbursement.model.Role.DEPARTMENT_MANAGER;
import static ch.uzh.csg.reimbursement.model.Role.FINANCE_ADMIN;
import static ch.uzh.csg.reimbursement.model.Role.HEAD_OF_INSTITUTE;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;

import javax.mail.internet.MimeMessage;
import javax.servlet.ServletContext;

import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import ch.uzh.csg.reimbursement.dto.EmailHeaderInfo;
import ch.uzh.csg.reimbursement.dto.ExpenseCountsDto;
import ch.uzh.csg.reimbursement.model.EmailReceiver;
import ch.uzh.csg.reimbursement.model.EmailSendJob;
import ch.uzh.csg.reimbursement.model.EmergencyEmailSendJob;
import ch.uzh.csg.reimbursement.model.Expense;
import ch.uzh.csg.reimbursement.model.NotificationSendJob;
import ch.uzh.csg.reimbursement.model.Role;
import ch.uzh.csg.reimbursement.model.User;
import ch.uzh.csg.reimbursement.repository.EmailReceiverRepositoryProvider;
import ch.uzh.csg.reimbursement.repository.ExpenseRepositoryProvider;
import ch.uzh.csg.reimbursement.repository.UserRepositoryProvider;

@Service
public class EmailService {

	private static final Logger LOG = LoggerFactory.getLogger(EmailService.class);

	@Autowired
	private ServletContext ctx;
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

	@Value("${mail.emergencyEmailAddress}")
	private String emergencyEmailAddress;

	@Value("${mail.inDebugMode}")
	private boolean inDebugMode;

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
				message.setText(body, true);
			}
		};
		this.mailSender.send(preparator);
		LOG.debug("Email sent to: "+sendJob.getHeaderInfo().getToEmail());
	}

	public void addToNotificationEmailReceiverQueue(User emailRecipient) {
		if(!emailReceiverProvider.contains(emailRecipient.getUid())){
			emailReceiverProvider.create(new EmailReceiver(emailRecipient.getUid()));
			LOG.debug("User added to the EmailReceiverQueue:"+emailRecipient.getFirstName()+" "+emailRecipient.getLastName()+" Roles: "+ emailRecipient.getRoles());
		}else{
			LOG.debug("User already in EmailReceiverQueue:" + emailRecipient.getEmail());
		}
	}

	public void sendEmergencyEmail(Exception ex){
		LOG.debug("Message: "+ex.getMessage());
		EmailHeaderInfo headerInfo = new EmailHeaderInfo(emergencyEmailAddress, "ReimbursementIFI", emergencyEmailAddress, "[reimbursement] Your attention is required!");
		EmergencyEmailSendJob emergencyEmailSendJob = new EmergencyEmailSendJob(headerInfo, defaultEmailTemplatePath, ex);

		if(inDebugMode){
			Template template = velocityEngine.getTemplate( emergencyEmailSendJob.getTemplatePath() );
			StringWriter writer = new StringWriter();
			template.merge( emergencyEmailSendJob.getContext(), writer );
			String body = writer.toString();

			long millis = System.currentTimeMillis();
			String path = ctx.getRealPath("/"+"Emergency"+"Email"+millis+".html");
			LOG.debug("Path to this email: "+path);
			try {
				FileWriter fw = new FileWriter(path);
				fw.write(body);
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			//in production
			processSendJob(emergencyEmailSendJob);
		}
	}


	@Scheduled(cron="${mail.sendOutEmailsCron}")
	@Async
	public void sendOutEmails(){
		for(EmailReceiver emailReceiver : emailReceiverProvider.findAll()){
			User user = userProvider.findByUid(emailReceiver.getUid());
			ExpenseCountsDto counts = getCountsForUser(user);

			EmailHeaderInfo headerInfo = new EmailHeaderInfo(defaultFromEmail, defaultFromName, user.getEmail(), defaultSubject);
			NotificationSendJob notification = new NotificationSendJob(headerInfo, notificationEmailTemplatePath, user,counts);

			if(inDebugMode){
				//TODO for testing
				LOG.debug("Sent to:" + user.getFirstName());
				LOG.debug("number of ownExpensesToSign:"+counts.getNumberOfOwnExpensesToSign());
				LOG.debug("number of expensesToAssign:"+counts.getNumberOfExpensesToBeAssigned());
				LOG.debug("number of expenseItemsToCheck:"+counts.getNumberOfExpensesToCheck());
				LOG.debug("number of expenseToSign:"+counts.getNumberOfExpensesToSign());
				LOG.debug("number of expenseToPrint:"+counts.getNumberOfOwnExpensesToPrint());


				Template template = velocityEngine.getTemplate( notification.getTemplatePath() );
				StringWriter writer = new StringWriter();
				template.merge( notification.getContext(), writer );
				String body = writer.toString();

				long millis = System.currentTimeMillis();
				String path = ctx.getRealPath("/"+user.getFirstName()+"Email"+millis+".html");
				LOG.debug("Path to this email: "+path);
				try {
					FileWriter fw = new FileWriter(path);
					fw.write(body);
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else{

				//in production
				processSendJob(notification);
			}
			emailReceiverProvider.delete(emailReceiver);
		}
		assert emailReceiverProvider.findAll().size() == 0;
		LOG.debug("All emails from the send queue have been sent successfully.");
	}

	private ExpenseCountsDto getCountsForUser(User user){
		if(user.getRoles().contains(FINANCE_ADMIN)){

			//expenses of the finance admin himself
			Set<Expense> ownExpensesToSign = expenseRepoProvider.findAllByStateForUser(TO_SIGN_BY_USER, user);
			Set<Expense> ownExpensesToPrint = expenseRepoProvider.findAllByStateForUser(SIGNED, user);

			//finance Admin Check
			Set<Expense> expensesNotAssignedToAnyone = expenseRepoProvider.findAllByStateWithoutUser(TO_BE_ASSIGNED, user);
			Set<Expense> expensesAssignedToFinanceAdmin = expenseRepoProvider.findAllByFinanceAdmin(user);
			Set<Expense> expensesAssignedToFinanceAdminStateToSign = new HashSet<Expense>();
			Set<Expense> expensesAssignedToFinanceAdminStateToCheck = new HashSet<Expense>();
			for(Expense expense : expensesAssignedToFinanceAdmin){
				if(expense.getState().equals(TO_SIGN_BY_FINANCE_ADMIN)){
					expensesAssignedToFinanceAdminStateToSign.add(expense);
				}else if(expense.getState().equals(ASSIGNED_TO_FINANCE_ADMIN)){
					expensesAssignedToFinanceAdminStateToCheck.add(expense);
				}
			}
			return new ExpenseCountsDto(expensesAssignedToFinanceAdminStateToCheck.size(),expensesAssignedToFinanceAdminStateToSign.size(), expensesNotAssignedToAnyone.size(), ownExpensesToSign.size(),ownExpensesToPrint.size());
		}else if(user.getRoles().contains(Role.PROF) || user.getRoles().contains(DEPARTMENT_MANAGER) || user.getRoles().contains(HEAD_OF_INSTITUTE)){

			//expenses of the manager himself
			Set<Expense> ownExpensesToSign = expenseRepoProvider.findAllByStateForUser(TO_SIGN_BY_USER, user);
			Set<Expense> ownExpensesToPrint = expenseRepoProvider.findAllByStateForUser(SIGNED, user);

			//Manager Checks
			Set<Expense> expensesAssignedToManager = expenseRepoProvider.findAllByAssignedManager(user);
			Set<Expense> expensesAssignedToManagerStateToSign = new HashSet<Expense>();
			Set<Expense> expensesAssignedToManagerStateToCheck = new HashSet<Expense>();
			for(Expense expense : expensesAssignedToManager){
				if(expense.getState().equals(TO_SIGN_BY_MANAGER)){
					expensesAssignedToManagerStateToSign.add(expense);
				}else if(expense.getState().equals(ASSIGNED_TO_MANAGER)){
					expensesAssignedToManagerStateToCheck.add(expense);
				}
			}
			return new ExpenseCountsDto(expensesAssignedToManagerStateToCheck.size(),expensesAssignedToManagerStateToSign.size(),0, ownExpensesToSign.size(),ownExpensesToPrint.size());
		}
		else{
			return new ExpenseCountsDto(0, 0,0,expenseRepoProvider.findAllByStateForUser(TO_SIGN_BY_USER, user).size(), expenseRepoProvider.findAllByStateForUser(SIGNED, user).size());
		}
	}
}