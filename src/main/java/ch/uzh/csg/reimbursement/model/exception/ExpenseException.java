package ch.uzh.csg.reimbursement.model.exception;

@SuppressWarnings("serial")
public abstract class ExpenseException extends BusinessException{
	public ExpenseException(String message){
		super(message);
	}
}
