package ch.uzh.csg.reimbursement.model;

import static ch.uzh.csg.reimbursement.model.ExpenseState.CREATED;
import static java.util.UUID.randomUUID;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;
import java.util.Set;

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

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@Table(name = "Expense")
@Transactional
@JsonIdentityInfo(
		generator = ObjectIdGenerators.PropertyGenerator.class,
		property = "uid")
public class Expense {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private int id;

	@Getter
	@Column(nullable = false, updatable = true, unique = true, name = "uid")
	private String uid;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@Getter
	@Setter
	@Column(nullable = false, updatable = true, unique = false, name = "date")
	private Date date;

	@Getter
	@Setter
	@Enumerated(STRING)
	@Column(nullable = true, updatable = true, unique = false, name = "state")
	private ExpenseState state;

	public double getTotalAmount() {
		double totalAmount=0;
		for(ExpenseItem item: getExpenseItems()){
			totalAmount += item.getAmount();
		}
		return totalAmount;
	}

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "contact_person_id")
	private User contactPerson;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "assigned_manager_id")
	private User assignedManager;

	@Getter
	@Setter
	@Column(nullable = false, updatable = true, unique = false, name = "booking_text")
	private String bookingText;

	@Getter
	@Setter
	@OneToMany(mappedBy = "expense", fetch = EAGER, cascade = CascadeType.ALL)
	private Set<Comment> comments;

	@Getter
	@Setter
	@OneToMany(mappedBy = "expense", fetch = EAGER, cascade = CascadeType.ALL)
	private Set<ExpenseItem> expenseItems;

	public Expense(User user, Date date, User contactPerson, String bookingText) {
		setUser(user);
		setDate(date);
		setState(CREATED);
		setContactPerson(contactPerson);
		setBookingText(bookingText);
		this.uid = randomUUID().toString();
	}

	public void updateExpense(Date date, User contactPerson, String bookingText, User assignedManager) {
		setDate(date);
		setContactPerson(contactPerson);
		setBookingText(bookingText);
		setAssignedManager(assignedManager);
	}

	/*
	 * The default constructor is needed by Hibernate, but should not be used at all.
	 */
	protected Expense() {
	}
}
