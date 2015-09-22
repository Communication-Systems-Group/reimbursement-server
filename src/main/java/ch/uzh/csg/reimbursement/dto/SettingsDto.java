package ch.uzh.csg.reimbursement.dto;

import lombok.Data;
import ch.uzh.csg.reimbursement.model.Language;

@Data
public class SettingsDto {

	private Language language;
	private String personnelNumber;
	private String phoneNumber;
	private boolean isActive;
}
