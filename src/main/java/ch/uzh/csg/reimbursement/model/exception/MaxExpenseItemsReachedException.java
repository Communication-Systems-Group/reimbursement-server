package ch.uzh.csg.reimbursement.model.exception;

@SuppressWarnings("serial")
public class MaxExpenseItemsReachedException extends ExpenseItemException {
	private final static String MESSAGE = "You have reached the max. number of expense-items for an expense.";
	public MaxExpenseItemsReachedException(){
		super(MESSAGE);
	}
}
