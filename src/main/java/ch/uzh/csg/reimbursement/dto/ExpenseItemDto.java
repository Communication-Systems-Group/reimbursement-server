package ch.uzh.csg.reimbursement.dto;

import java.util.Date;

import lombok.Data;

@Data
public class ExpenseItemDto {

	private Date date;
	private String costCategoryUid;
	private String explanation;
	private String currency;
	private double originalAmount;
	private String project;
}