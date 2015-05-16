package ch.uzh.csg.reimbursement.model.exception;

@SuppressWarnings("serial")
public class ServiceException extends RuntimeException{
	private final static String MESSAGE = "An error on the server part of the system occued.";
	public ServiceException(){
		super(MESSAGE);
	}
}
