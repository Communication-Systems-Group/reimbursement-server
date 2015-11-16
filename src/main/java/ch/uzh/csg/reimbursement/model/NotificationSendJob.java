package ch.uzh.csg.reimbursement.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.VelocityContext;

import ch.uzh.csg.reimbursement.dto.EmailHeaderInfo;
import ch.uzh.csg.reimbursement.dto.ExpenseCountsDto;

public class NotificationSendJob extends EmailSendJob {

	private User receivingUser;
	private int numberOfExpensesToCheck;
	private int numberOfExpensesToSign;
	private int numberOfExpensesToBeAssigned;
	private int numberOfOwnExpensesToSign;

	public NotificationSendJob(EmailHeaderInfo headerInfo, String templatePath, User receivingUser, ExpenseCountsDto counts) {
		super(headerInfo, templatePath);
		this.receivingUser = receivingUser;
		this.numberOfExpensesToCheck = counts.getNumberOfExpensesToCheck();
		this.numberOfExpensesToSign = counts.getNumberOfExpensesToSign();
		this.numberOfExpensesToBeAssigned = counts.getNumberOfExpensesToBeAssigned();
		this.numberOfOwnExpensesToSign = counts.getNumberOfOwnExpensesToSign();
	}

	@Override
	public VelocityContext getContext() {
		VelocityContext context = new VelocityContext();

		Map<String, String> headerLink = new HashMap<String, String>();
		headerLink.put("address", "http://ifi.uzh.ch");
		headerLink.put("text", "Login to Reimbursement IFI");
		context.put("headerLink", headerLink);

		context.put("greeting", "Hei " + receivingUser.getFirstName());
		context.put("lead", "Long since we saw you the last time!");
		context.put("message", "You have a lot of work to do:");
		if (numberOfExpensesToCheck > 0) {
			context.put("numberOfExpensesToCheck",numberOfExpensesToCheck );
		}
		if (numberOfExpensesToSign > 0) {
			context.put("numberOfExpensesToSign", numberOfExpensesToSign);
		}
		if (numberOfExpensesToBeAssigned > 0) {
			context.put("numberOfExpensesToBeAssigned", numberOfExpensesToBeAssigned);
		}
		if ( numberOfOwnExpensesToSign> 0) {
			context.put("numberOfOwnExpensesToSign", numberOfOwnExpensesToSign);
		}

		context.put("callout", "Don't wait longer!");

		Map<String, String> calloutLink = new HashMap<String, String>();
		calloutLink.put("address", "http://ifi.uzh.ch");
		calloutLink.put("text", "Login to Reimbursement IFI");
		context.put("calloutLink", calloutLink);

		Map<String, String> lastFooterLink = new HashMap<String, String>();
		lastFooterLink.put("address", "http://ifi.uzh.ch");
		lastFooterLink.put("text", "Login to Reimbursement IFI");
		context.put("lastFooterLink", lastFooterLink);

		return context;
	}

}
