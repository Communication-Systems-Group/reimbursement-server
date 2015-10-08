package ch.uzh.csg.reimbursement.dto;

import lombok.Data;

@Data
public class ExpenseStateStatisticsDto {

	private int totalAmountOfExpenses;
	private int draft;
	private int assignedToProf;
	private int rejected;
	private int toBeAssigned;
	private int assignedToFinanceAdmin;
	private int toSignByUser;
	private int toSignByProf;
	private int toSignByFinanceAdmin;
	private int signed;
	private int printed;

}
