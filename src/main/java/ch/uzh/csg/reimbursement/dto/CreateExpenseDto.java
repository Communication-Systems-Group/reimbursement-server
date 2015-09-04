package ch.uzh.csg.reimbursement.dto;

import lombok.Data;
import ch.uzh.csg.reimbursement.model.ExpenseState;

@Data
public class CreateExpenseDto {

	private String accounting;
	private ExpenseState state;
}
