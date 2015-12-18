package ch.uzh.csg.reimbursement.dto;

import java.util.Map;

import lombok.Data;

@Data
public class ExpenseStateStatisticsDto {

	private int totalNumberOfExpenses;
	private int draft;
	private int assignedToManager;
	private int rejected;
	private int toBeAssigned;
	private int assignedToFinanceAdmin;
	private int toSignByUser;
	private int toSignByManager;
	private int toSignByFinanceAdmin;
	private int signed;
	private int printed;
	private int archived;
	private double percentageArchived;
	private Map<String, Double> monthlyTotalAmounts;
}
