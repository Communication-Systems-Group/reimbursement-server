package ch.uzh.csg.reimbursement.model.exception;

@SuppressWarnings("serial")
public class TokenExpiredException extends TokenException {
	private final static String MESSAGE = "The token has been exipred.";
	public TokenExpiredException() {
		super(MESSAGE);
	}
}
