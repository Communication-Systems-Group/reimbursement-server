
package ch.uzh.csg.reimbursement.model.exception;

@SuppressWarnings("serial")
public class ExpenseDeleteViolationException extends ExpenseException {
	private final static String MESSAGE = "This expense cannot be deleted because of its state.";

	public ExpenseDeleteViolationException() {
		super(MESSAGE);
	}
}
