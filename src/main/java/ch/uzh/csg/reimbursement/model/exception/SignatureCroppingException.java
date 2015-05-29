package ch.uzh.csg.reimbursement.model.exception;

@SuppressWarnings("serial")
public class SignatureCroppingException extends SignatureException {
	private final static String MESSAGE = "An error occured while cropping the signature.";
	public SignatureCroppingException(){
		super(MESSAGE);
	}
}
