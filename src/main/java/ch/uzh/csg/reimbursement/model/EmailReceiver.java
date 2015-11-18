package ch.uzh.csg.reimbursement.model;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

@Entity
@Table(name = "EmailReceiver_")
@Transactional
public class EmailReceiver {

	@Transient
	private final Logger LOG = LoggerFactory.getLogger(EmailReceiver.class);

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private int id;

	@Getter
	@Column(nullable = false, updatable = true, unique = false, name = "uid")
	private String uid;

	/*
	 * The default constructor is needed by Hibernate, but should not be used at
	 * all.
	 */
	protected EmailReceiver() {
	}

	public EmailReceiver(String receiver_uid) {
		uid = receiver_uid;
	}
}
