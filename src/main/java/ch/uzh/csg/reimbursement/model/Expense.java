package ch.uzh.csg.reimbursement.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.UUID;

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
	@JoinColumn(name = "user_uid")
	private User user;

	@Getter
	@Setter
	@Column(nullable = false, updatable = true, unique = false, name = "date")
	private String date;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "contact_person_uid")
	private User contactPerson;

	@Getter
	@Setter
	@Column(nullable = false, updatable = true, unique = false, name = "booking_text")
	private String bookingText;

	public Expense(User user, String date, User contactPerson, String bookingText) {
		setUser(user);
		setDate(date);
		setContactPerson(contactPerson);
		setBookingText(bookingText);
		this.uid = UUID.randomUUID().toString();
	}

	public void updateExpense(User user, String date, User contactPerson, String bookingText) {
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
