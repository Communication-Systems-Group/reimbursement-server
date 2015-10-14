package ch.uzh.csg.reimbursement.dto;

import lombok.Data;

@Data
public class CostCategoryDto {

	private CostCategoryTranslationDto name;
	private CostCategoryTranslationDto description;
	private CostCategoryTranslationDto accountingPolicy;
	private int accountNumber;
}
