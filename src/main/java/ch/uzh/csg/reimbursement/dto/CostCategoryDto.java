package ch.uzh.csg.reimbursement.dto;

import lombok.Data;

@Data
public class CostCategoryDto {

	private String name;
	private String description;
	private String accountingPolicy;
}
