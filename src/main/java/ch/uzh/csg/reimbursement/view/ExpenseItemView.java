package ch.uzh.csg.reimbursement.view;

import java.util.Date;

import lombok.Data;

@Data
public class ExpenseItemView {
	private Date date;
	private double account;
	private String description;
	private AmountView amount;
	private String cost_center;
}