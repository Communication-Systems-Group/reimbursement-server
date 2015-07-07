package ch.uzh.csg.reimbursement.view;

import lombok.Data;

@Data
public class ReviewExpenseView {

	private String uid;
	private UserView creator;
	private String date;
	private int amount;
	private String account;

}
