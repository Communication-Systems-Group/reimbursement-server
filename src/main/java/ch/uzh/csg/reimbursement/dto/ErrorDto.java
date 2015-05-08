package ch.uzh.csg.reimbursement.dto;

import lombok.Data;

@Data
public class ErrorDto {

	private String type;
	private String url;

	public ErrorDto(StringBuffer url, RuntimeException ex) {
		this.type = ex.getClass().getSimpleName();
		this.url = url.toString();
	}
}
