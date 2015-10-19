package ch.uzh.csg.reimbursement.model;

import static java.util.Calendar.MILLISECOND;
import static java.util.Calendar.MONTH;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "Token_")
@Transactional
@JsonIgnoreProperties(value = { "type", "user", "created" })
public class Token {

	@Transient
	private final Logger LOG = LoggerFactory.getLogger(Token.class);

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private int id;

	@Getter
	@Column(nullable = false, updatable = false, unique = true, name = "uid")
	private String uid;

	@Getter
	@Enumerated(STRING)
	@Column(nullable = false, updatable = false, unique = false, name = "type")
	private TokenType type;

	@Getter
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@Getter
	@Setter
	@Column(nullable = true, updatable = true, unique = false, name = "content")
	private String content;

	@Getter
	@Column(nullable = false, updatable = false, unique = false, name = "created")
	private Calendar created;

	public Token(TokenType type, User user) {
		this.type = type;
		this.user = user;
		generateNewUid();
		setCreatedToNow();
	}

	public Token(TokenType type, User user, String content) {
		this(type, user);
		this.content = content;
	}

	public boolean isExpiredInMilliseconds(int expirationInMilliseconds) {
		Calendar calMinusExpiration = new GregorianCalendar();
		calMinusExpiration.add(MILLISECOND, -expirationInMilliseconds);

		return calMinusExpiration.after(created);
	}

	public boolean isExpiredInMonths(int expirationInMonths) {
		Calendar calMinusExpiration = new GregorianCalendar();
		calMinusExpiration.add(MONTH, -expirationInMonths);

		return calMinusExpiration.after(created);
	}

	public void setCreatedToNow() {
		this.created = new GregorianCalendar();
	}

	public void generateNewUid() {
		this.uid = UUID.randomUUID().toString();
	}


	/*
	 * The default constructor is needed by Hibernate, but should not be used at
	 * all.
	 */
	protected Token() {
	}
}
