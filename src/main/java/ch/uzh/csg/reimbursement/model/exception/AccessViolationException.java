package ch.uzh.csg.reimbursement.model.exception;

@SuppressWarnings("serial")
public class AccessViolationException extends ExpenseException {
	private final static String MESSAGE = "The logged in user has no access to this resource.";

	public AccessViolationException() {
		super(MESSAGE);
	}
}
