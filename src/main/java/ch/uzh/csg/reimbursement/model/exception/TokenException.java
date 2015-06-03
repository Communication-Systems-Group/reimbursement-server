package ch.uzh.csg.reimbursement.model.exception;

@SuppressWarnings("serial")
public abstract class TokenException extends BusinessException {
	public TokenException(String message) {
		super(message);
	}
}
