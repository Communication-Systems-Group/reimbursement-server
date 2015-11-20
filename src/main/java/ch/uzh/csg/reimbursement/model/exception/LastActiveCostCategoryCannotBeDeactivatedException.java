package ch.uzh.csg.reimbursement.model.exception;

@SuppressWarnings("serial")
public class LastActiveCostCategoryCannotBeDeactivatedException extends BusinessException {
	private final static String MESSAGE = "The last active cost category cannot be deactived.";
	public LastActiveCostCategoryCannotBeDeactivatedException(){
		super(MESSAGE);
	}
}

