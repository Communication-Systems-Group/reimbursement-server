package ch.uzh.csg.reimbursement.model.exception;

@SuppressWarnings("serial")
public class UserException extends BusinessException{
	public UserException(String message){
		super(message);
	}
}
