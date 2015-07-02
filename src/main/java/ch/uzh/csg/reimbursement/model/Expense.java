package ch.uzh.csg.reimbursement.model;

import static ch.uzh.csg.reimbursement.model.ExpenseState.CREATED;
import static java.util.UUID.randomUUID;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;
import java.util.HashSet;
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

@Entity
@Table(name = "Expense")
@Transactional
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

	@Getter
	@Setter
	@Column(nullable = true, updatable = true, unique = false, name = "total_amount")
	private double totalAmount;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "contact_person_id")
	private User contactPerson;

	@Getter
	@Setter
	@Column(nullable = false, updatable = true, unique = false, name = "booking_text")
	private String bookingText;

	@Getter
	@Setter
	@Column(nullable = true, updatable = true, unique = false, name = "expense_comment")
	private double expenseComment;

	@Getter
	@OneToMany(mappedBy = "expense", fetch = EAGER, cascade = CascadeType.ALL)
	private Set<ExpenseItem> expenseItems = new HashSet<ExpenseItem>();

	public Expense(User user, Date date, User contactPerson, String bookingText) {
		setUser(user);
		setDate(date);
		setState(CREATED);
		setTotalAmount(0);
		setContactPerson(contactPerson);
		setBookingText(bookingText);
		this.uid = randomUUID().toString();
	}

	public void updateExpense(Date date, User contactPerson, String bookingText) {
		setDate(date);
		setContactPerson(contactPerson);
		setBookingText(bookingText);
	}

	/*
	 * The default constructor is needed by Hibernate, but should not be used at all.
	 */
	protected Expense() {
	}
}
