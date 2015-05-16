package ch.uzh.csg.reimbursement.dto;

import lombok.Data;

@Data
public class ExpenseDto {

	private String date;
	private String bookingText;
	private String userUid;
}
