package ch.uzh.csg.reimbursement.dto;

import lombok.Data;

@Data
public class SearchDto {

	private String lastName;
	private String role;
	private String costCategory;
	private String sapText;
}
