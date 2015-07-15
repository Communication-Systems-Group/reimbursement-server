package ch.uzh.csg.reimbursement.model.exception;

@SuppressWarnings("serial")
public class ExpenseItemNotFoundException extends ExpenseItemException {
	private final static String MESSAGE = "The expenseItem could not be found.";
	public ExpenseItemNotFoundException(){
		super(MESSAGE);
	}
}
