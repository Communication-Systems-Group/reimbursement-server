package ch.uzh.csg.reimbursement.dto;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ErrorDto {

	private String type ="undefined";
	private String url="";
	private String message;

	public ErrorDto(StringBuffer url, RuntimeException ex) {
		this.type = ex.getClass().getName();
		this.message = ex.getLocalizedMessage();
		this.url = url.toString();
	}
}
