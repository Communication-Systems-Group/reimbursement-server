package ch.uzh.csg.reimbursement.view;

import java.util.Date;

import lombok.Data;
import ch.uzh.csg.reimbursement.model.ExpenseState;

@Data
public class ExpenseView {

	private String uid;
	private Date date;
	private ExpenseState state;
	private double amount;
	private String account;

}
