package ch.uzh.csg.reimbursement.model.exception;

@SuppressWarnings("serial")
public class PdfExportException extends BusinessException {
	private final static String MESSAGE = "Pdf has not been generated yet.";
	public PdfExportException() {
		super(MESSAGE);
	}
}
