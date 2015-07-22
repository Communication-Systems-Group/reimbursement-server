package ch.uzh.csg.reimbursement.model.exception;

@SuppressWarnings("serial")
public class ExpenseAccessViolationException extends ExpenseException {
	private final static String MESSAGE = "The logged in user has no access to this expense.";

	public ExpenseAccessViolationException() {
		super(MESSAGE);
	}
}
