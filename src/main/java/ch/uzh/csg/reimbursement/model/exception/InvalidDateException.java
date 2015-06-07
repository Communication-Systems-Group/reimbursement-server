package ch.uzh.csg.reimbursement.model.exception;

@SuppressWarnings("serial")
public class InvalidDateException extends BusinessException {
	private final static String MESSAGE_START = "The date";
	private final static String MESSAGE_END = "is not valid.";

	public InvalidDateException(String date) {
		super(createMessage(date));
	}

	private static String createMessage(String date) {
		return MESSAGE_START + " '" + date + "' " + MESSAGE_END;
	}
}
