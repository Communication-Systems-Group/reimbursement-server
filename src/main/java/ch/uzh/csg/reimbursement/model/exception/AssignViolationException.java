package ch.uzh.csg.reimbursement.model.exception;

@SuppressWarnings("serial")
public class AssignViolationException extends ExpenseException {
	private final static String MESSAGE = "The expense cannot be assigned to the given user.";

	public AssignViolationException() {
		super(MESSAGE);
	}
}
