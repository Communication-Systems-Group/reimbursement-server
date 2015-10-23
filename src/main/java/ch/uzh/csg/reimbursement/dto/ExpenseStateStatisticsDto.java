package ch.uzh.csg.reimbursement.dto;

import java.util.Date;

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
	private double percentagePrinted;
	private Double totalAmountFirstQuarter;
	private Double totalAmountSecondQuarter;
	private Double totalAmountThirdQuarter;
	private Double totalAmountFourthQuarter;
	public  Date startDate;

}
