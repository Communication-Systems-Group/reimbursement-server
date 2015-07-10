package ch.uzh.csg.reimbursement.model.exception;


@SuppressWarnings("serial")
public class AttachmentWithNoContentFoundException extends BusinessException {
	private final static String MESSAGE = "There is no attachment for the specified expenseItem.";
	public AttachmentWithNoContentFoundException() {
		super(MESSAGE);
	}
}
