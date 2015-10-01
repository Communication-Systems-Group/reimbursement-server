package ch.uzh.csg.reimbursement.model.exception;

@SuppressWarnings("serial")
public class UnexpectedStateException extends BusinessException{
	private final static String MESSAGE = "The expense is in an undefined state.";
	public UnexpectedStateException(){
		super(MESSAGE);
	}
}
