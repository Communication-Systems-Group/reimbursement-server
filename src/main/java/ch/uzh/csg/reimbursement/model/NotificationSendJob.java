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
	private int numberOfOwnExpensesToPrint;

	//EN
	private String greeting = "Hi";
	private String lead = "There are new expenses in your dashboard that require your attention:";
	private String message = "You have a lot of work to do:";
	private String numberOfExpensesToCheck_label = "Assigned expenses to validate:";
	private String numberOfExpensesToSign_label = "Number of expenses to be signed:";
	private String numberOfExpensesToBeAssigned_label = "Number of expenses to be assigned";
	private String numberOfOwnExpensesToSign_label = "Own expenses to sign:";
	private String numberOfOwnExpensesToPrint_label = "Own expenses to print:";
	private String callout = "See your dashboard here:";


	//DE
	private String greeting_de = "Hallo";
	private String lead_de = "Es sind neue Spesen in Deinem Dashboard:";
	private String message_de = "Arbeit wartet auf Dich: ";
	private String numberOfExpensesToCheck_label_de = "Zu validierende Spesen:";
	private String numberOfExpensesToSign_label_de = "Zu signierende Spesen:";
	private String numberOfExpensesToBeAssigned_label_de = "Nicht zugewiesene Spesen:";
	private String numberOfOwnExpensesToSign_label_de = "Eigene Spesen zum Signieren:";
	private String numberOfOwnExpensesToPrint_label_de = "Eigene Spesen zum Ausdrucken:";
	private String callout_de = "Hier geht's zum Dashboard:";




	public NotificationSendJob(EmailHeaderInfo headerInfo, String templatePath, User receivingUser, ExpenseCountsDto counts) {
		super(headerInfo, templatePath);
		this.receivingUser = receivingUser;
		numberOfExpensesToCheck = counts.getNumberOfExpensesToCheck();
		numberOfExpensesToSign = counts.getNumberOfExpensesToSign();
		numberOfExpensesToBeAssigned = counts.getNumberOfExpensesToBeAssigned();
		numberOfOwnExpensesToSign = counts.getNumberOfOwnExpensesToSign();
		numberOfOwnExpensesToPrint= counts.getNumberOfOwnExpensesToPrint();
	}

	@Override
	public VelocityContext getContext() {

		if(receivingUser.getLanguage() == Language.DE){
			greeting = greeting_de;
			lead = lead_de;
			message = message_de;
			numberOfExpensesToCheck_label = numberOfExpensesToCheck_label_de;
			numberOfExpensesToSign_label = numberOfExpensesToSign_label_de;
			numberOfExpensesToBeAssigned_label = numberOfExpensesToBeAssigned_label_de;
			numberOfOwnExpensesToSign_label = numberOfOwnExpensesToSign_label_de;
			numberOfOwnExpensesToPrint_label = numberOfOwnExpensesToPrint_label_de;
			callout = callout_de;
		}

		VelocityContext context = new VelocityContext();

		Map<String, String> headerLink = new HashMap<String, String>();
		headerLink.put("address", "http://192.41.136.228/#!/welcome");
		headerLink.put("text", "Login to Reimbursement IFI");
		context.put("headerLink", headerLink);

		context.put("greeting", greeting+" "+ receivingUser.getFirstName());
		context.put("lead", lead);
		//		context.put("message", message);


		context.put("numberOfExpensesToCheck_label", numberOfExpensesToCheck_label);
		context.put("numberOfExpensesToSign_label", numberOfExpensesToSign_label);
		context.put("numberOfExpensesToBeAssigned_label", numberOfExpensesToBeAssigned_label);
		context.put("numberOfOwnExpensesToSign_label", numberOfOwnExpensesToSign_label);
		context.put("numberOfOwnExpensesToPrint_label", numberOfOwnExpensesToPrint_label);

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
		if ( numberOfOwnExpensesToPrint> 0) {
			context.put("numberOfOwnExpensesToPrint", numberOfOwnExpensesToPrint);
		}

		context.put("callout", callout);

		Map<String, String> calloutLink = new HashMap<String, String>();
		calloutLink.put("address", "http://192.41.136.228/#!/dashboard");
		calloutLink.put("text", "Dashboard");
		context.put("calloutLink", calloutLink);

		Map<String, String> lastFooterLink = new HashMap<String, String>();
		lastFooterLink.put("address", "http://192.41.136.228");
		lastFooterLink.put("text", "Login to Reimbursement IFI");
		context.put("lastFooterLink", lastFooterLink);

		return context;
	}

}
