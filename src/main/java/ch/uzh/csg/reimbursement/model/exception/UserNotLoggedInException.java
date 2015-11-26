package ch.uzh.csg.reimbursement.model.exception;

@SuppressWarnings("serial")
public class UserNotLoggedInException extends UserException {
	private final static String MESSAGE = "The requested user is not logged in.";
	public UserNotLoggedInException(){
		super(MESSAGE);
	}
}