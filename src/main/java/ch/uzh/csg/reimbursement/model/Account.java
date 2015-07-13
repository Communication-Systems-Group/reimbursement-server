package ch.uzh.csg.reimbursement.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.UUID;

import javax.persistence.CascadeType;
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

import ch.uzh.csg.reimbursement.view.View;

import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "Account")
@Transactional
public class Account {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private int id;

	@JsonView(View.SummaryWithUid.class)
	@Getter
	@Column(nullable = false, updatable = true, unique = true, name = "uid")
	private String uid;

	@Getter
	@Setter
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "cost_category_id")
	private CostCategory category;

	@Getter
	@Setter
	@Column(nullable = false, updatable = true, unique = false, name = "number")
	private int number;

	public Account(CostCategory category, int number) {
		setCategory(category);
		setNumber(number);
		this.uid = UUID.randomUUID().toString();
	}

	/*
	 * The default constructor is needed by Hibernate, but should not be used at all.
	 */
	protected Account() {
	}
}
