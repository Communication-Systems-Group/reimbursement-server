package ch.uzh.csg.reimbursement.model;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;
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
@Table(name = "Token")
@Transactional
@JsonIgnoreProperties(value = {"type", "user", "created"})
public class Token {

	@Transient
	private final Logger LOG = LoggerFactory.getLogger(Token.class);

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private int id;

	@Getter
	@Setter
	@Column(nullable = false, updatable = false, unique = true, name = "uid")
	private String uid;

	@Getter
	@Setter
	@Enumerated(STRING)
	@Column(nullable = false, updatable = false, unique = false, name = "type")
	private TokenType type;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@Getter
	@Column(nullable = false, updatable = false, unique = false, name = "created")
	private Date created;

	public Token(TokenType type, User user) {
		this.uid = UUID.randomUUID().toString();
		this.created = new Date();
		this.type = type;
		this.user = user;
	}

	/*
	 * The default constructor is needed by Hibernate, but should not be used at all.
	 */
	protected Token() {
	}
}
