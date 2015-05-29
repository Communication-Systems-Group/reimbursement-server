package ch.uzh.csg.reimbursement.model.exception;

@SuppressWarnings("serial")
public class SignatureNotFoundException extends SignatureException {
	private final static String MESSAGE = "The signature could not be found.";
	public SignatureNotFoundException(){
		super(MESSAGE);
	}
}
