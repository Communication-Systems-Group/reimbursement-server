package ch.uzh.csg.reimbursement.model.exception;

@SuppressWarnings("serial")
public class AccessException extends ExpenseException {
	private final static String MESSAGE = "The logged in user has no access to this resource.";

	public AccessException() {
		super(MESSAGE);
	}
}
