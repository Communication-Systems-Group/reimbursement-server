package ch.uzh.csg.reimbursement.model.exception;

@SuppressWarnings("serial")
public abstract class SignatureException extends BusinessException{
	public SignatureException(String message){
		super(message);
	}
}
