package ch.uzh.csg.reimbursement.view;

import java.util.Date;

import lombok.Data;
import ch.uzh.csg.reimbursement.model.CostCategory;
import ch.uzh.csg.reimbursement.model.ExpenseItemState;

@Data
public class ExpenseItemView {
	private String uid;
	private Date date;
	private CostCategory costCategory;
	private String reason;
	private ExpenseItemState state;
	private AmountView amount;
	private String costCenter;
}