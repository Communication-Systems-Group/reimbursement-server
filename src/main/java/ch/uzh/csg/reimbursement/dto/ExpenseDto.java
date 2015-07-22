package ch.uzh.csg.reimbursement.dto;

import lombok.Data;
import ch.uzh.csg.reimbursement.model.ExpenseState;

@Data
public class ExpenseDto {

	private String accounting;
	private String assignedManagerUid;
	private ExpenseState state;
}
