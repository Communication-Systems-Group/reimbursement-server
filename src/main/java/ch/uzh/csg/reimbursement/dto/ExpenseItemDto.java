package ch.uzh.csg.reimbursement.dto;

import java.util.Date;

import lombok.Data;

@Data
public class ExpenseItemDto {

	private Date date;
	private String costCategoryUid;
	private String reason;
	private String currency;
	private double exchangeRate;
	private double originalAmount;
	private double calculatedAmount;
	private String project;
}