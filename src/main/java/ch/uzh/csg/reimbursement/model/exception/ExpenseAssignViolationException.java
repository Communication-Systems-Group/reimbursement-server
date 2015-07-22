package ch.uzh.csg.reimbursement.model.exception;

@SuppressWarnings("serial")
public class ExpenseAssignViolationException extends ExpenseException {
	private final static String MESSAGE = "It is not possible to assign an expense with this resource.";

	public ExpenseAssignViolationException() {
		super(MESSAGE);
	}
}
