package ch.uzh.csg.reimbursement.dto;

import lombok.Data;
import ch.uzh.csg.reimbursement.model.Expense;

@Data
public class ExpensePdfDto {

	private Expense expense;
	private String url;
	private String qrcode;
	private String financeAdminSignature;
	private String financeAdminIsProf;
	private String assignedManagerSignature;
	private String userSignature;

	public ExpensePdfDto(Expense expense, String url, String qrcode, String userSignature, String financeAdminSignature, String assignedManagerSignature, boolean financeAdminIsProf) {
		this.expense = expense;
		this.url = url;
		this.qrcode = qrcode;
		this.financeAdminSignature = financeAdminSignature;
		if(financeAdminIsProf) {
			this.financeAdminIsProf = "true";
		} else {
			this.financeAdminIsProf = "false";
		}
		this.assignedManagerSignature = assignedManagerSignature;
		this.userSignature = userSignature;
	}
}
