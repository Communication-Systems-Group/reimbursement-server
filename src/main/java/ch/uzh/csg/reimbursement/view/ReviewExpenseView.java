package ch.uzh.csg.reimbursement.view;

import java.util.Date;

import lombok.Data;

@Data
public class ReviewExpenseView {

	private String uid;
	private UserView creator;
	private Date date;
	private double amount;
	private String account;

}
