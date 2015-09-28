package ch.uzh.csg.reimbursement.model.exception;

@SuppressWarnings("serial")
public class PdfExportViolationException extends BusinessException {
	private final static String MESSAGE = "Pdf has never been exported.";
	public PdfExportViolationException() {
		super(MESSAGE);
	}
}
