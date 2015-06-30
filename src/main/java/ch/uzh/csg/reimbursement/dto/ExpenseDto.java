package ch.uzh.csg.reimbursement.dto;

import java.util.Date;

import lombok.Data;

@Data
public class ExpenseDto {

	private Date date;
	private String bookingText;
	private String contactPersonUid;

}
