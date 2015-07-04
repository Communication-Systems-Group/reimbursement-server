package ch.uzh.csg.reimbursement.model.exception;

@SuppressWarnings("serial")
public class CostCategoryNotFoundException extends CostCategoryException {
	private final static String MESSAGE = "No costCategory could be found.";
	public CostCategoryNotFoundException(){
		super(MESSAGE);
	}
}
