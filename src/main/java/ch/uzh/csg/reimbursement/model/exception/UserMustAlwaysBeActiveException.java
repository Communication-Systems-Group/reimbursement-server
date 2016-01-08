package ch.uzh.csg.reimbursement.model.exception;

@SuppressWarnings("serial")
public class UserMustAlwaysBeActiveException extends UserException {
	private final static String MESSAGE = "This user must because of it's role always be active.";
	public UserMustAlwaysBeActiveException(){
		super(MESSAGE);
	}
}
