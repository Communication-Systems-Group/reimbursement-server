package ch.uzh.csg.reimbursement.model.exception;

@SuppressWarnings("serial")
public abstract class BusinessException extends RuntimeException{
	public BusinessException(String message){
		super(message);
	}
}
