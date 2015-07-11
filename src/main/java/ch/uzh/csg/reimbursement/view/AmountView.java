package ch.uzh.csg.reimbursement.view;

import lombok.Data;

@Data
public class AmountView {
	private double original;
	private double exchange_rate;
	private String currency;

	public AmountView(double original, double exchange_rate, String currency) {
		this.original = original;
		this.exchange_rate = exchange_rate;
		this.currency = currency;
	}
}
