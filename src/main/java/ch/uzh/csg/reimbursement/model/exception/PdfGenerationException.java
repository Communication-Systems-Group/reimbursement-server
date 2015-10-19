package ch.uzh.csg.reimbursement.model.exception;

@SuppressWarnings("serial")
public class PdfGenerationException extends BusinessException {
	private final static String MESSAGE = "The PDF cannot be generated in this state.";
	public PdfGenerationException() {
		super(MESSAGE);
	}
}
