package ch.uzh.csg.reimbursement.model.exception;

@SuppressWarnings("serial")
public class UserNotFoundException extends BusinessException {
	private final static String MESSAGE = "The user could not be found.";
	public UserNotFoundException(){
		super(MESSAGE);
	}
}
