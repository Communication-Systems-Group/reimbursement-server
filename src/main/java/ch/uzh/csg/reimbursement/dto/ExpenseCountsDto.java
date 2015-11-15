package ch.uzh.csg.reimbursement.dto;

import lombok.Data;

@Data
public class ExpenseCountsDto {
	private int numberOfExpensesToCheck;
	private int numberOfPdfsToSign;
	private int numberOfExpensesToBeAssigned;


	public ExpenseCountsDto(int expensesToCheck, int pdfToSign, int numberOfExpensesToBeAssigned) {
		this.numberOfExpensesToCheck = expensesToCheck;
		this.numberOfPdfsToSign = pdfToSign;
		this.numberOfExpensesToBeAssigned = numberOfExpensesToBeAssigned;
	}
}
