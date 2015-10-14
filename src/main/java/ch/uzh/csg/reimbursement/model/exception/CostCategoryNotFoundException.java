package ch.uzh.csg.reimbursement.model.exception;

@SuppressWarnings("serial")
public class CostCategoryNotFoundException extends BusinessException {
	private final static String MESSAGE = "No costCategory could be found.";
	public CostCategoryNotFoundException(){
		super(MESSAGE);
	}
}

