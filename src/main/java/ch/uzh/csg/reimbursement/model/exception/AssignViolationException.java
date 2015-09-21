package ch.uzh.csg.reimbursement.model.exception;

@SuppressWarnings("serial")
public class AssignViolationException extends ExpenseException {
	private final static String MESSAGE = "Expenses without expensetems cannot be assigned.";

	public AssignViolationException() {
		super(MESSAGE);
	}
}
