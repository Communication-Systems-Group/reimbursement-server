package ch.uzh.csg.reimbursement.model.exception;

@SuppressWarnings("serial")
public class ExpenseItemNotFoundException extends ExpenseItemException {
	private final static String MESSAGE = "No expenseItems could be found.";
	public ExpenseItemNotFoundException(){
		super(MESSAGE);
	}
}
