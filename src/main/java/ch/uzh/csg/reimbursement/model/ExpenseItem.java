package ch.uzh.csg.reimbursement.model;

import static ch.uzh.csg.reimbursement.model.ExpenseItemState.CREATED;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.springframework.transaction.annotation.Transactional;


@Entity
@Table(name = "ExpenseItem")
@Transactional
public class ExpenseItem {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private int id;

	@Getter
	@Setter
	@Column(nullable = false, updatable = true, unique = false, name = "uid")
	private String uid;

	@Getter
	@Setter
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "expense_id", insertable = false, updatable = false)
	private Expense expense;

	@Getter
	@Setter
	@Column(nullable = false, updatable = true, unique = false, name = "date")
	private Date date;

	@Getter
	@Setter
	@Enumerated(STRING)
	@Column(nullable = false, updatable = true, unique = false, name = "state")
	private ExpenseItemState state;

	@Getter
	@Setter
	@Column(nullable = true, updatable = true, unique = false, name = "amount")
	private double amount;

	@Getter
	@Setter
	@Column(nullable = true, updatable = true, unique = false, name = "cost_category")
	private String costCategory;

	@Getter
	@Setter
	@Column(nullable = true, updatable = true, unique = false, name = "reason")
	private String reason;

	@Getter
	@Setter
	@Column(nullable = true, updatable = true, unique = false, name = "currency")
	private String currency;

	@Getter
	@Setter
	@Column(nullable = true, updatable = true, unique = false, name = "exchange_rate")
	private double exchangeRate;

	@Getter
	@Setter
	@Column(nullable = true, updatable = true, unique = false, name = "project")
	private String project;

	@Getter
	@Setter
	@Column(nullable = true, updatable = true, unique = false, name = "expense_item_comment")
	private double expenseItemComment;

	public ExpenseItem(Date date, String costCategory, String reason, String currency, double exchangeRate, double amount, String project) {
		this.uid = UUID.randomUUID().toString();
		setDate(date);
		setState(CREATED);
		setAmount(amount);
		setCostCategory(costCategory);
		setReason(reason);
		setCurrency(currency);
		setExchangeRate(exchangeRate);
		setProject(project);
	}

	public void updateExpenseItem(Date date, String costCategory, String reason, String currency, double exchangeRate, double amount, String project){
		setDate(date);
		setAmount(amount);
		setCostCategory(costCategory);
		setReason(reason);
		setCurrency(currency);
		setExchangeRate(exchangeRate);
		setProject(project);
	}

	/*
	 * The default constructor is needed by Hibernate, but should not be used at all.
	 */
	protected ExpenseItem() {
	}
}