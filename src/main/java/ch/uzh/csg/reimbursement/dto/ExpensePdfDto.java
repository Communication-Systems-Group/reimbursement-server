package ch.uzh.csg.reimbursement.dto;

import lombok.Data;
import ch.uzh.csg.reimbursement.model.Expense;

@Data
public class ExpensePdfDto {

	private Expense expense;
	private String url;
	private String qrcode;
	

	public ExpensePdfDto(Expense expense, String url, char[] qrcode) {
		setExpense(expense);
		setUrl(url);
		setQrcode(new String(qrcode));
	}
}
