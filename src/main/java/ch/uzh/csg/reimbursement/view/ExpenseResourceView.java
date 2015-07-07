package ch.uzh.csg.reimbursement.view;

import java.util.Set;

import lombok.Data;

@Data
public class ExpenseResourceView {

	private String uid;
	private Set<ExpenseView> myExpenseItems;
	private Set<ReviewExpenseView> myReviewExpenseItems;

}
