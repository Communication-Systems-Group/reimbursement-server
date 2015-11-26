package ch.uzh.csg.reimbursement.model.exception;

@SuppressWarnings("serial")
public class AssignException extends ExpenseException {
	private final static String MESSAGE = "The expense cannot be assigned to the given user. Either the expense has no expense-items or there are expense-items with empty project fields.";

	public AssignException() {
		super(MESSAGE);
	}
}
