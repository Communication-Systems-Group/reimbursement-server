package ch.uzh.csg.reimbursement.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.VelocityContext;

import ch.uzh.csg.reimbursement.dto.EmailHeaderInfo;
import lombok.Setter;

public class TestEmailSendJob extends EmailSendJob{

	public TestEmailSendJob(EmailHeaderInfo headerInfo, String templatePath) {
		super(headerInfo, templatePath);
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
		context.put("greeting", "Hello John");
		context.put("lead", "Phasellus dictum sapien a neque luctus cursus. Pellentesque sem dolor, fringilla et pharetra vitae.");
		context.put("message", "Phasellus dictum sapien a neque luctus cursus. Pellentesque sem dolor, fringilla et pharetra vitae. consequat vel lacus. Sed iaculis pulvinar ligula, ornare fringilla ante viverra et. ");
		context.put("callout", "Phasellus dictum sapien a neque luctus cursus. Pellentesque sem dolor, fringilla et pharetra vitae.");

		Map<String, String> calloutLink = new HashMap<String, String>();
		calloutLink.put("address", "/calloutLink");
		calloutLink.put("text", "CalloutLinkText");
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
