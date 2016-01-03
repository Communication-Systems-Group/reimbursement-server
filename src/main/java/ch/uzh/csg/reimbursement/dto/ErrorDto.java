package ch.uzh.csg.reimbursement.dto;

import lombok.Data;

@Data
public class ErrorDto {

	private String type;
	private String message;

	public ErrorDto(Exception ex) {
		this.type = ex.getClass().getSimpleName();
		this.message = ex.getMessage();
	}
}
