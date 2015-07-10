package ch.uzh.csg.reimbursement.model.exception;


@SuppressWarnings("serial")
public class ExpenseItemAttachmentNotFoundException extends BusinessException {
	private final static String MESSAGE = "There is no attachment for the specified expenseItem.";
	public ExpenseItemAttachmentNotFoundException() {
		super(MESSAGE);
	}
}
