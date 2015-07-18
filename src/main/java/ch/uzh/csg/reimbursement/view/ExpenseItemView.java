package ch.uzh.csg.reimbursement.view;

import java.util.Date;

import lombok.Data;
import ch.uzh.csg.reimbursement.model.CostCategory;

@Data
public class ExpenseItemView {
	private String uid;
	private Date date;
	private CostCategory costCategory;
	private String reason;
	private AmountView amount;
	private String cost_center;
}