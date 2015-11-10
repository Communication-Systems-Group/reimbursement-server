package ch.uzh.csg.reimbursement.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.VelocityContext;

import ch.uzh.csg.reimbursement.dto.EmailHeaderInfo;

public class NotificationSendJob extends EmailSendJob{


	private User receivingUser;
	private int numberExpenseItems = 0;
	private int numberPdfItems = 0;

	public NotificationSendJob(EmailHeaderInfo headerInfo, String templatePath, User receivingUser) {
		super(headerInfo, templatePath);
		this.receivingUser =  receivingUser;
	}

	public NotificationSendJob(EmailHeaderInfo headerInfo, String templatePath, User receivingUser, int numberExpenseItems, int numberPdfItems) {
		super(headerInfo, templatePath);
		this.numberExpenseItems = numberExpenseItems;
		this.numberPdfItems = numberPdfItems;
	}

	public void addExpenseItem(){
		this.numberExpenseItems++;
	}

	public void addPdfItem(){
		this.numberPdfItems++;
	}

	@Override
	public VelocityContext getContext(){
		VelocityContext context = new VelocityContext();

		String numberExpenseItemsAsString = "3";
		String numberOfPdfGenerations = "2";
		String foo = "blub";

		context.put("greeting", "Hei "+receivingUser.getFirstName());
		context.put("lead", "Long since we saw you the last time!");
		context.put("message", "You have a lot of work to do:");
		context.put("newexpenseitems", numberExpenseItemsAsString);
		context.put("newpdftosign", numberOfPdfGenerations);
		context.put("foo", foo);
		context.put("callout", "Don't wait longer!");

		Map calloutLink = new HashMap();
		calloutLink.put("address", "/calloutLink");
		calloutLink.put("text", "Login to Reimbursement IFI");
		context.put("calloutLink", calloutLink);

		return context;
	}

}
