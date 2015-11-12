package ch.uzh.csg.reimbursement.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.VelocityContext;

import ch.uzh.csg.reimbursement.dto.EmailHeaderInfo;

public class NotificationSendJob extends EmailSendJob {

	private User receivingUser;
	private int numberExpenseItems = 0;
	private int numberPdfItems = 0;

	public NotificationSendJob(EmailHeaderInfo headerInfo, String templatePath, User receivingUser) {
		super(headerInfo, templatePath);
		this.receivingUser = receivingUser;
	}

	public NotificationSendJob(EmailHeaderInfo headerInfo, String templatePath, User receivingUser,
			int numberExpenseItems, int numberPdfItems) {
		super(headerInfo, templatePath);
		this.numberExpenseItems = numberExpenseItems;
		this.numberPdfItems = numberPdfItems;
	}

	public void increaseExpenseItemCounter() {
		this.numberExpenseItems++;
	}

	public void increasePdfItemCounter() {
		this.numberPdfItems++;
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
		if (numberExpenseItems > 0) {
			context.put("newexpenseitems", numberExpenseItems);
		}
		if (numberPdfItems > 0) {
			context.put("newpdftosign", numberPdfItems);
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
