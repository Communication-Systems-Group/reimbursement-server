package ch.uzh.csg.reimbursement.model.exception;

@SuppressWarnings("serial")
public class ValidationException extends BusinessException {
	
	private final static String MESSAGE = "The passed string did not match the regex for key: ";
	
	public ValidationException(String key){
		super(MESSAGE + key);
	}
}