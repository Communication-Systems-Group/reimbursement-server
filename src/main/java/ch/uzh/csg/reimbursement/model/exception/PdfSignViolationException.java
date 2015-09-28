package ch.uzh.csg.reimbursement.model.exception;

@SuppressWarnings("serial")
public class PdfSignViolationException extends BusinessException {
	private final static String MESSAGE = "The uploaded Pdf has not been signed.";
	public PdfSignViolationException() {
		super(MESSAGE);
	}
}
