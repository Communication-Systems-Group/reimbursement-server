package ch.uzh.csg.reimbursement.model.exception;

@SuppressWarnings("serial")
public class NoDateGivenException extends ExpenseItemException {
	private final static String MESSAGE = "The date should not be null.";
	public NoDateGivenException(){
		super(MESSAGE);
	}
}
