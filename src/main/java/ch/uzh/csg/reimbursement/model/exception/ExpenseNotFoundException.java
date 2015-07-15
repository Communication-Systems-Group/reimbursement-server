package ch.uzh.csg.reimbursement.model.exception;

@SuppressWarnings("serial")
public class ExpenseNotFoundException extends ExpenseException {
	private final static String MESSAGE = "The expense could not be found.";
	public ExpenseNotFoundException(){
		super(MESSAGE);
	}
}
