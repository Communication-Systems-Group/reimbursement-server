package ch.uzh.csg.reimbursement.dto;

import lombok.Data;

@Data
public class ExpenseCountsDto {
	private int numberOfExpensesToCheck;
	private int numberOfExpensesToSign;
	private int numberOfExpensesToBeAssigned;
	private int numberOfOwnExpensesToSign;


	public ExpenseCountsDto(int expensesToCheck, int pdfToSign, int numberOfExpensesToBeAssigned, int numberOfOwnExpensesToSign) {
		this.numberOfExpensesToCheck = expensesToCheck;
		this.numberOfExpensesToSign = pdfToSign;
		this.numberOfExpensesToBeAssigned = numberOfExpensesToBeAssigned;
		this.numberOfOwnExpensesToSign = numberOfOwnExpensesToSign;
	}
}
