package ch.uzh.csg.reimbursement.model.exception;

@SuppressWarnings("serial")
public class PdfSignException extends BusinessException {
	private final static String MESSAGE = "The uploaded Pdf has not been signed.";
	public PdfSignException() {
		super(MESSAGE);
	}
}
