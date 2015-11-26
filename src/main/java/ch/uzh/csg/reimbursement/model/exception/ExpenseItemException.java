package ch.uzh.csg.reimbursement.model.exception;

@SuppressWarnings("serial")
public abstract class ExpenseItemException extends BusinessException{
	public ExpenseItemException(String message){
		super(message);
	}
}
