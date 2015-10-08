package ch.uzh.csg.reimbursement.dto;

import lombok.Data;

@Data
public class ExpenseStateStatisticsDto {

	private double draft;
	private double assignedToProf;
	private double rejected;
	private double toBeAssigned;
	private double assignedToFinanceAdmin;
	private double toSignByUser;
	private double toSignByProf;
	private double toSignByFinanceAdmin;
	private double signed;
	private double printed;

}
