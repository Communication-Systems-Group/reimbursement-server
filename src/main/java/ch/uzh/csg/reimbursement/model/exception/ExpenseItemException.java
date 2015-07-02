package ch.uzh.csg.reimbursement.model.exception;

@SuppressWarnings("serial")
public class ExpenseItemException extends BusinessException{
	public ExpenseItemException(String message){
		super(message);
	}
}
