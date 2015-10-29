package ch.uzh.csg.reimbursement.dto;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

public class Templateinformation {

	@Getter
	@Setter
	private String templatePath;
	@Getter
	@Setter
	private Map<String, Object> model;

	public Templateinformation(String templatePath, Map<String, Object> model) {
		super();
		this.templatePath = templatePath;
		this.model = model;
	}

}
