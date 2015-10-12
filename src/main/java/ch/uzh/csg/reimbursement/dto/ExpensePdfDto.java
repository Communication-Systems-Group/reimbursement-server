package ch.uzh.csg.reimbursement.dto;

import lombok.Data;
import ch.uzh.csg.reimbursement.model.Expense;

@Data
public class ExpensePdfDto {

	private Expense expense;
	private String url;
	private String qrcode;
	private String assignedManagerSignature;
	private String userSignature;

	public ExpensePdfDto(Expense expense, String url, String qrcode, String assignedManagerSignature, String userSignature) {
		this.expense = expense;
		this.url = url;
		this.qrcode = qrcode;
		this.assignedManagerSignature = assignedManagerSignature;
		this.userSignature = userSignature;
	}

}
