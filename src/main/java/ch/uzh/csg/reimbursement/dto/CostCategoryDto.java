package ch.uzh.csg.reimbursement.dto;

import lombok.Data;
import ch.uzh.csg.reimbursement.model.CostCategoryAccountingPolicy;
import ch.uzh.csg.reimbursement.model.CostCategoryDescription;
import ch.uzh.csg.reimbursement.model.CostCategoryName;

@Data
public class CostCategoryDto {

	private CostCategoryName name;
	private CostCategoryDescription description;
	private CostCategoryAccountingPolicy accountingPolicy;
}
