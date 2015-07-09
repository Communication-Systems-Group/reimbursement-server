package ch.uzh.csg.reimbursement.view;

import lombok.Data;

@Data
public class AmountView {
	private double original;
	private double exchange_rate;
	private double chf;
}
