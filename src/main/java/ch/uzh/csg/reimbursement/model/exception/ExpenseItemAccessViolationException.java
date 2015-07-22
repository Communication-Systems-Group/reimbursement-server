package ch.uzh.csg.reimbursement.model.exception;

@SuppressWarnings("serial")
public class ExpenseItemAccessViolationException extends ExpenseItemException {
	private final static String MESSAGE = "The logged in user has no access to this expenseItem.";
	public ExpenseItemAccessViolationException(){
		super(MESSAGE);
	}
}