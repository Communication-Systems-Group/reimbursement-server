package ch.uzh.csg.reimbursement.dto;

import lombok.Data;
import ch.uzh.csg.reimbursement.model.Expense;

@Data
public class ExpenseUrlDto {

	private Expense expense;
	private String url;

	public ExpenseUrlDto(Expense expense, String url) {
		setExpense(expense);
		setUrl(url);
	}
}
