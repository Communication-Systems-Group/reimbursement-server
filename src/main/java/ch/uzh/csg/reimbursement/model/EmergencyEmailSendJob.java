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

	private String serverProtocolAndIp;
	private Exception ex;
	private String greeting = "Bad News!";
	private String lead = "An unexpectet error hinders the reimbursement ifi system to perfom correctly. Please do not hesitate to correct the situation.";
	private String message = "You receive this message because your email address is registered as support email address.";
	private String callout = "Click here to go to the startpace of the system:";

	public EmergencyEmailSendJob(EmailHeaderInfo headerInfo, String templatePath, Exception ex, String serverProtocolAndIp) {
		super(headerInfo, templatePath);
		this.ex = ex;
		this.serverProtocolAndIp = serverProtocolAndIp;
	}

	@Setter
	private String templatePath;

	@Override
	public VelocityContext getContext() {
		VelocityContext context = new VelocityContext();

		/* create our list of links  */
		ArrayList<Map<String,String>> headerLinkList = new ArrayList<Map<String,String>>();
		Map<String, String> map = new HashMap<String, String>();
		map.put("address", serverProtocolAndIp+"/#!/dashboard");
		map.put("text", "Login");
		headerLinkList.add( map );

		map = new HashMap<String, String>();
		map.put("address", serverProtocolAndIp+"/#!/settings");
		map.put("text", "Settings");
		headerLinkList.add( map );

		map = new HashMap<String, String>();
		map.put("address", serverProtocolAndIp+"/#!/user-guide");
		map.put("text", "User-Guide");
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
		calloutLink.put("address", serverProtocolAndIp+"/#!/dashboard");
		calloutLink.put("text", "Dashboard");
		context.put("calloutLink", calloutLink);

		/* create our list of links  */
		ArrayList<Map<String,String>> footerLinkList = new ArrayList<Map<String,String>>();
		map = new HashMap<String, String>();
		map.put("address", serverProtocolAndIp+"/#!/dashboard");
		map.put("text", "Login");
		footerLinkList.add( map );

		map = new HashMap<String, String>();
		map.put("address", serverProtocolAndIp+"/#!/settings");
		map.put("text", "Settings");
		footerLinkList.add( map );

		context.put("footerLinkList", footerLinkList);

		Map<String, String> lastFooterLink = new HashMap<String, String>();
		lastFooterLink.put("address", serverProtocolAndIp+"/#!/user-guide");
		lastFooterLink.put("text", "User Guide");
		context.put("lastFooterLink", lastFooterLink);

		return context;
	}
}
