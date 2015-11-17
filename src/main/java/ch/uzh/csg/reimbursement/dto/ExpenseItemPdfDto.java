package ch.uzh.csg.reimbursement.dto;

import lombok.Data;

@Data
public class ExpenseItemPdfDto {

	private String costCategoryName;
	private int accountNumber;
	private double originalAmount;
	private String project;
}