package ch.uzh.csg.reimbursement.model.exception;

@SuppressWarnings("serial")
public class PdfExportViolationException extends BusinessException {
	private final static String MESSAGE = "Pdf has not been generated yet.";
	public PdfExportViolationException() {
		super(MESSAGE);
	}
}
