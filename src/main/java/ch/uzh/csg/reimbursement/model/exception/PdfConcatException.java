package ch.uzh.csg.reimbursement.model.exception;

@SuppressWarnings("serial")
public class PdfConcatException extends BusinessException {
	private final static String MESSAGE = "The PDF cannot be concatenated.";
	public PdfConcatException() {
		super(MESSAGE);
	}
}