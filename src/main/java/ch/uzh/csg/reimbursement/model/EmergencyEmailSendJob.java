package ch.uzh.csg.reimbursement.model;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.VelocityContext;

import ch.uzh.csg.reimbursement.dto.EmailHeaderInfo;
import lombok.Setter;

public class EmergencyEmailSendJob extends EmailSendJob{

	private Throwable ex;
	private String greeting = "Bad News!";
	private String lead = "An unexpectet error hinders the reimbursement ifi system to perfom correctly. Please do not hesitate to correct the situation.";
	private String message = "You receive this message because your email address is registered as support email address.";
	private String callout = "Click here to go to the startpace of the system:";

	public EmergencyEmailSendJob(EmailHeaderInfo headerInfo, String templatePath, Throwable ex2) {
		super(headerInfo, templatePath);
		this.ex = ex2;
	}

	public EmergencyEmailSendJob(EmailHeaderInfo headerInfo, String templatePath, String greeting, String lead,String message, String callout) {
		super(headerInfo, templatePath);
		this.greeting = greeting;
		this.lead = lead;
		this.message = message;
		this.callout = callout;
	}

	@Setter
	private String templatePath;

	@Override
	public VelocityContext getContext() {
		VelocityContext context = new VelocityContext();

		/* create our list of links  */
		ArrayList<Map<String,String>> headerLinkList = new ArrayList<Map<String,String>>();
		Map<String, String> map = new HashMap<String, String>();
		map.put("address", "/login");
		map.put("text", "Login");
		headerLinkList.add( map );

		map = new HashMap<String, String>();
		map.put("address", "/settings");
		map.put("text", "Settings");
		headerLinkList.add( map );

		map = new HashMap<String, String>();
		map.put("address", "/user");
		map.put("text", "User");
		headerLinkList.add( map );

		context.put("headerLinkList", headerLinkList);
		context.put("greeting", greeting);
		context.put("lead", lead);
		context.put("message", message);
		context.put("callout", callout);

		if(ex != null){
			context.put("exceptionMessage", ex.getLocalizedMessage());
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			ex.printStackTrace(pw);
			context.put("exceptionStackTrace", sw.toString());
		}


		Map<String, String> calloutLink = new HashMap<String, String>();
		calloutLink.put("address", "/calloutLink");
		calloutLink.put("text", "Dashboard");
		context.put("calloutLink", calloutLink);

		/* create our list of links  */
		ArrayList<Map<String,String>> footerLinkList = new ArrayList<Map<String,String>>();
		map = new HashMap<String, String>();
		map.put("address", "/login");
		map.put("text", "Login");
		footerLinkList.add( map );

		map = new HashMap<String, String>();
		map.put("address", "/settings");
		map.put("text", "Settings");
		footerLinkList.add( map );

		map = new HashMap<String, String>();
		map.put("address", "/user");
		map.put("text", "User");
		footerLinkList.add( map );

		context.put("footerLinkList", footerLinkList);

		Map<String, String> lastFooterLink = new HashMap<String, String>();
		lastFooterLink.put("address", "/Last");
		lastFooterLink.put("text", "Last");
		context.put("lastFooterLink", lastFooterLink);

		return context;
	}
}
