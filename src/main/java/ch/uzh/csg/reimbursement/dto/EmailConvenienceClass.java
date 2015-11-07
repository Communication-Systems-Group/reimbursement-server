package ch.uzh.csg.reimbursement.dto;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailConvenienceClass {

	private  String fromEmail;
	private  String fromName;
	private  String toEmail;
	private  String subject;
	private  String ccEmail;
	private  String bccEmail;
	private  String replyToEmail;
	private String templatePath;
	private Map<String, Object> messageModel;

	public EmailConvenienceClass(){
		super();
	}

	public EmailConvenienceClass(String fromEmail, String fromName, String toEmail, String subject) {
		super();
		this.fromEmail = fromEmail;
		this.fromName = fromName;
		this.toEmail = toEmail;
		this.subject = subject;
	}


	public EmailConvenienceClass(String fromEmail, String fromName, String toEmail, String subject, String templatePath,
			Map<String, Object> messageModel) {
		super();
		this.fromEmail = fromEmail;
		this.fromName = fromName;
		this.toEmail = toEmail;
		this.subject = subject;
		this.templatePath = templatePath;
		this.messageModel = messageModel;
	}



	public boolean isSetCcEmail(){
		return !ccEmail.isEmpty();
	}
	public boolean isSetBccEmail(){
		return !bccEmail.isEmpty();
	}
	public boolean isSetReplyToEmail(){
		return !replyToEmail.isEmpty();
	}
	public boolean isSetTemplatePath(){
		return !templatePath.isEmpty();
	}
	public boolean isSetMessageModel(){
		return !messageModel.isEmpty();
	}


}
