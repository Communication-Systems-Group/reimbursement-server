package ch.uzh.csg.reimbursement.dto;

import lombok.Data;
import ch.uzh.csg.reimbursement.model.ExpenseState;

@Data
public class ExpenseDto {

	private String bookingText;
	private String assignedManagerUid;
	private ExpenseState state;
}
