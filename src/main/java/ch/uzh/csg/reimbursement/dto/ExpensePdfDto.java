package ch.uzh.csg.reimbursement.dto;

import java.util.ArrayList;

import lombok.Value;
import ch.uzh.csg.reimbursement.model.Expense;

@Value
public class ExpensePdfDto {

	private Expense expense;
	private ArrayList<ExpenseItemPdfDto> expenseItemsPdfDto;
	private String url;
	private String qrcode;
	private String financeAdminSignature;
	private String assignedManagerSignature;
	private String userSignature;
	private Boolean managerHasRoleProf;

}
