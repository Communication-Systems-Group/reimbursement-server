package ch.uzh.csg.reimbursement.view;

import java.util.Date;

import lombok.Data;

@Data
public class ExpenseItemView {
	private String uid;
	private Date date;
	private String costCategory;
	private String description;
	private AmountView amount;
	private String cost_center;
}