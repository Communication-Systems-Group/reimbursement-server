package ch.uzh.csg.reimbursement.model;

import static ch.uzh.csg.reimbursement.model.ExpenseState.CREATED;
import static java.util.UUID.randomUUID;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

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

	public Expense(User user, Date date, User contactPerson, String bookingText) {
		setUser(user);
		setDate(date);
		setState(CREATED);
		setTotalAmount(0);
		setContactPerson(contactPerson);
		setBookingText(bookingText);
		this.uid = randomUUID().toString();
	}

	public void updateExpense(User user, Date date, User contactPerson, String bookingText) {
		setUser(user);
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
