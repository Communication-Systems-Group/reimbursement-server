package ch.uzh.csg.reimbursement.dto;

import lombok.Data;

@Data
public class SearchExpenseDto {

	private String lastName;
	private String role;
	private String costCategory;
	private String accountingText;

}
