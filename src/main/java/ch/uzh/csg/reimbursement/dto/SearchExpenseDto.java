package ch.uzh.csg.reimbursement.dto;

import java.util.Date;

import lombok.Data;

@Data
public class SearchExpenseDto {

	private String lastName;
	private String role;
	private String accountingText;
	private String expenseState;
	private Date startTime;
	private Date endTime;

}
