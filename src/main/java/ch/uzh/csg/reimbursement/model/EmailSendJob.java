package ch.uzh.csg.reimbursement.model;

import org.apache.velocity.VelocityContext;

import ch.uzh.csg.reimbursement.dto.EmailHeaderInfo;
import lombok.Getter;

public abstract class EmailSendJob {

	@Getter
	private EmailHeaderInfo headerInfo;

	@Getter
	private String templatePath;
	public abstract VelocityContext getContext();

	public EmailSendJob(EmailHeaderInfo headerInfo, String templatePath) {
		super();
		this.headerInfo = headerInfo;
		this.templatePath = templatePath;
	}


}
