package ch.uzh.csg.reimbursement.model.exception;

@SuppressWarnings("serial")
public class NotSupportedFileTypeException extends ExpenseItemException {
	private final static String MESSAGE = "The uploaded file type is not supported.";
	public NotSupportedFileTypeException(){
		super(MESSAGE);
	}
}
