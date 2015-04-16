package ch.uzh.csg.reimbursement.server.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "User")
public class User {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private int id;

	@Getter
	@Column(nullable = false, updatable = true, unique = true, name = "uid")
	private String uid;

	@Getter
	@Setter
	@Column(nullable = false, updatable = true, unique = false, name = "first_name")
	private String firstName;

	@Getter
	@Setter
	@Column(nullable = false, updatable = true, unique = false, name = "last_name")
	private String lastName;

	public User(String firstName, String lastName) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.uid = UUID.randomUUID().toString();
	}

	/*
	 * The default constructor is needed by Hibernate, but should not be used at all.
	 */
	protected User() {
	}
}
