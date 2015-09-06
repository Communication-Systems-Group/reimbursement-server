package ch.uzh.csg.reimbursement.model;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.springframework.transaction.annotation.Transactional;

import ch.uzh.csg.reimbursement.serializer.UserSerializer;
import ch.uzh.csg.reimbursement.view.View;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
@Table(name = "Expense")
@Transactional
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "uid")
public class Expense {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private int id;

	@JsonView(View.SummaryWithUid.class)
	@Getter
	@Column(nullable = false, updatable = true, unique = true, name = "uid")
	private String uid;

	@JsonView(View.Summary.class)
	@JsonSerialize(using = UserSerializer.class)
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@JsonView(View.DashboardSummary.class)
	@Getter
	@Setter
	@Column(nullable = false, updatable = true, unique = false, name = "date")
	private Date date;

	@JsonView(View.DashboardSummary.class)
	@Getter
	@Setter
	@Enumerated(STRING)
	@Column(nullable = true, updatable = true, unique = false, name = "state")
	private ExpenseState state;

	@JsonView(View.Summary.class)
	@JsonSerialize(using = UserSerializer.class)
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "finance_admin_id")
	private User financeAdmin;

	@JsonView(View.Summary.class)
	@JsonSerialize(using = UserSerializer.class)
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "assigned_manager_id")
	private User assignedManager;

	@JsonView(View.DashboardSummary.class)
	@Getter
	@Setter
	@Column(nullable = false, updatable = true, unique = false, name = "accounting")
	private String accounting;

	@JsonView(View.DashboardSummary.class)
	public double getTotalAmount() {
		double totalAmount = 0;
		for (ExpenseItem item : getExpenseItems()) {
			totalAmount += item.getCalculatedAmount();
		}
		return totalAmount;
	}

	@Getter
	@Setter
	@OneToMany(mappedBy = "expense", fetch = EAGER, cascade = CascadeType.ALL)
	private Set<Comment> comments;

	@Getter
	@Setter
	@OneToMany(mappedBy = "expense", fetch = EAGER, orphanRemoval=true)
	private Set<ExpenseItem> expenseItems;

	public Expense(User user, Date date, User financeAdmin, String accounting, ExpenseState state) {
		setUser(user);
		setDate(date);
		setState(state);
		setFinanceAdmin(financeAdmin);
		setAccounting(accounting);
		this.uid = UUID.randomUUID().toString();
	}

	public void updateExpense(Date date, User financeAdmin, String accounting, User assignedManager,
			ExpenseState state) {
		setDate(date);
		setFinanceAdmin(financeAdmin);
		setAccounting(accounting);
		setAssignedManager(assignedManager);
		setState(state);
	}

	/*
	 * The default constructor is needed by Hibernate, but should not be used at
	 * all.
	 */
	protected Expense() {
	}
}
