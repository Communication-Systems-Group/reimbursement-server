package ch.uzh.csg.reimbursement.dto;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class ExpenseItemPdfDto {

	private String costCategoryName;
	private int accountNumber;
	private String project;
	private double totalAmount;

	public ExpenseItemPdfDto(String costCategoryName, int accountNumber, String project, double totalAmount) {
		this.costCategoryName = costCategoryName;
		this.accountNumber = accountNumber;
		this.project = project;
		this.totalAmount = totalAmount;
	}

	public void addAmount(double amount) {
		this.totalAmount += amount;
	}
}