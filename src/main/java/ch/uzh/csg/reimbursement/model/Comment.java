package ch.uzh.csg.reimbursement.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.springframework.transaction.annotation.Transactional;

@Entity
@Table(name = "Comment")
@Transactional
public class Comment {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private int id;

	@Getter
	@Column(nullable = false, updatable = true, unique = true, name = "uid")
	private String uid;

	@Getter
	@Setter
	@Column(nullable = false, updatable = true, unique = false, name = "date")
	private Date date;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@Getter
	@Setter
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "expense_id")
	private Expense expense;

	@Getter
	@Setter
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "expense_item_id")
	private ExpenseItem expenseItem;

	@Getter
	@Setter
	@Column(nullable = false, updatable = true, unique = false, name = "text")
	private String text;

	public Comment(Date date, User user, Expense expense, String text) {
		setDate(date);
		setUser(user);
		setExpense(expense);
		setText(text);
		this.uid = UUID.randomUUID().toString();
	}

	public Comment(Date date, User user, ExpenseItem expenseItem, String text) {
		setDate(date);
		setUser(user);
		setExpenseItem(expenseItem);
		setText(text);
		this.uid = UUID.randomUUID().toString();
	}

	/*
	 * The default constructor is needed by Hibernate, but should not be used at all.
	 */
	protected Comment() {
	}
}
