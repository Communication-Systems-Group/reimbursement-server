package ch.uzh.csg.reimbursement.model.exception;

@SuppressWarnings("serial")
public class TokenNotFoundException extends TokenException {
	private final static String MESSAGE = "The token has not been found.";
	public TokenNotFoundException() {
		super(MESSAGE);
	}
}
