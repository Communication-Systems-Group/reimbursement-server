package ch.uzh.csg.reimbursement.model.exception;

@SuppressWarnings("serial")
public class PdfGenerationException extends BusinessException {
	private final static String MESSAGE = "The PDF cannot be generated.";
	public PdfGenerationException() {
		super(MESSAGE);
	}

	public PdfGenerationException(String message) {
		super(message);
	}
}
