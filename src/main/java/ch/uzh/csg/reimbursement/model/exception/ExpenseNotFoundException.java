package ch.uzh.csg.reimbursement.model.exception;

@SuppressWarnings("serial")
public class ExpenseNotFoundException extends ExpenseException {
	private final static String MESSAGE = "No expenses could be found.";
	public ExpenseNotFoundException(){
		super(MESSAGE);
	}
}
