package ch.uzh.csg.reimbursement.view;

import lombok.Data;

@Data
public class AmountView {
	private double originalAmount;
	private double exchange_rate;
	private String currency;
	private double calculatedAmount;

	public AmountView(double originalAmount, double calculatedAmount, double exchange_rate, String currency) {
		this.originalAmount = originalAmount;
		this.calculatedAmount = calculatedAmount;
		this.exchange_rate = exchange_rate;
		this.currency = currency;
	}
}
