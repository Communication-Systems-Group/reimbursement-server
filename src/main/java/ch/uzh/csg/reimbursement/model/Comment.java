package ch.uzh.csg.reimbursement.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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
	@Setter
	@Column(nullable = false, updatable = true, unique = false, name = "date")
	private Date date;

	@Getter
	@Setter
	@Column(nullable = false, updatable = true, unique = false, name = "text")
	private String text;

	public Comment(Date date, String text) {
		setDate(date);
		setText(text);
	}

	/*
	 * The default constructor is needed by Hibernate, but should not be used at all.
	 */
	protected Comment() {
	}
}
