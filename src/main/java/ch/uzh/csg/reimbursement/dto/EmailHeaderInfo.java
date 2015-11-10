package ch.uzh.csg.reimbursement.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailHeaderInfo {

	private  String fromEmail;
	private  String fromName;
	private  String toEmail;
	private  String subject;
	private  String ccEmail;
	private  String bccEmail;
	private  String replyToEmail;

	public EmailHeaderInfo(String fromEmail, String fromName, String toEmail, String subject) {
		super();
		this.fromEmail = fromEmail;
		this.fromName = fromName;
		this.toEmail = toEmail;
		this.subject = subject;
	}

	public boolean isSetCcEmail(){
		return ccEmail!=null && !ccEmail.isEmpty();
	}
	public boolean isSetBccEmail(){
		return bccEmail!=null && !bccEmail.isEmpty();
	}
	public boolean isSetReplyToEmail(){
		return replyToEmail !=null && !replyToEmail.isEmpty();
	}
}
