package ch.uzh.csg.reimbursement.dto;

import java.util.Date;
import java.util.Set;

import lombok.Value;
import ch.uzh.csg.reimbursement.model.Expense;

@Value
public class ExpensePdfDto implements IPdfDto{

	private Expense expense;
	private Set<ExpenseItemPdfDto> expenseItemsPdfDto;
	private String url;
	private String qrcode;
	private String financeAdminSignature;
	private String assignedManagerSignature;
	private String userSignature;
	private Boolean managerHasRoleProf;
	private Date guestViewExpirationDate;

}
