package ch.uzh.csg.reimbursement.model.exception;

@SuppressWarnings("serial")
public class NotSupportedCurrencyException extends ExpenseItemException {
	private final static String MESSAGE = "The given currency is not supported.";
	public NotSupportedCurrencyException(){
		super(MESSAGE);
	}
}
