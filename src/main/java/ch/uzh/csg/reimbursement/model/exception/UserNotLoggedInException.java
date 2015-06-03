package ch.uzh.csg.reimbursement.model.exception;

@SuppressWarnings("serial")
public class UserNotLoggedInException extends UserException {

	public UserNotLoggedInException(String message) {
		super(message);
	}

}
