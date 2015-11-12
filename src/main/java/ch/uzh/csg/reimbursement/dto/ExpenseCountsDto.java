package ch.uzh.csg.reimbursement.dto;

import lombok.Data;

@Data
public class ExpenseCountsDto {
	private int numberOfExpensesToCheck;
	private int numberOfPdfsToSign;

	public ExpenseCountsDto(int expensesToCheck, int pdfToSign) {
		this.numberOfExpensesToCheck = expensesToCheck;
		this.numberOfPdfsToSign = pdfToSign;
	}
}
