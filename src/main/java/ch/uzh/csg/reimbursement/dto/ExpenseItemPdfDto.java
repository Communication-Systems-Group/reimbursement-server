package ch.uzh.csg.reimbursement.dto;

import static java.util.UUID.randomUUID;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class ExpenseItemPdfDto {

	private String uid;
	private String costCategoryName;
	private int accountNumber;
	private String project;
	private double totalAmount;
	private int relevant;

	public ExpenseItemPdfDto(String costCategoryName, int accountNumber, String project, double totalAmount, int relevant) {
		this.uid = randomUUID().toString();
		this.costCategoryName = costCategoryName;
		this.accountNumber = accountNumber;
		this.project = project;
		this.totalAmount = totalAmount;
		this.relevant = relevant;
	}

	public void addAmount(double amount) {
		this.totalAmount += amount;
	}
}