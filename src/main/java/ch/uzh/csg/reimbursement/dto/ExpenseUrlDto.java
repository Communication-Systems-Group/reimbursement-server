package ch.uzh.csg.reimbursement.dto;

import lombok.Data;
import ch.uzh.csg.reimbursement.model.Expense;

@Data
public class ExpenseUrlDto {

	private Expense expense;
	private String url;
	private String qrcode;

	public ExpenseUrlDto(Expense expense, String url, char[] qrcode) {
		setExpense(expense);
		setUrl(url);
		setQrcode(new String(qrcode));
	}
}
