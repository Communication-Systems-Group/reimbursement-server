package ch.uzh.csg.reimbursement.dto;

import lombok.Data;

@Data
public class AccessRights {

	private boolean viewable;
	private boolean editable;
	private boolean signable;
}
