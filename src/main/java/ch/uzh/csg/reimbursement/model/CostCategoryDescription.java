package ch.uzh.csg.reimbursement.model;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.springframework.transaction.annotation.Transactional;

@Entity
@Table(name = "CostCategoryDescription_")
@Transactional
public class CostCategoryDescription {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private int id;

	@Getter
	@Setter
	@Column(nullable = true, updatable = true, unique = false, name = "de")
	private String de;

	@Getter
	@Setter
	@Column(nullable = true, updatable = true, unique = false, name = "en")
	private String en;

	public CostCategoryDescription(String de, String en) {
		setDe(de);
		setEn(en);
	}

	/*
	 * The default constructor is needed by Hibernate, but should not be used at
	 * all.
	 */
	protected CostCategoryDescription() {
	}
}
