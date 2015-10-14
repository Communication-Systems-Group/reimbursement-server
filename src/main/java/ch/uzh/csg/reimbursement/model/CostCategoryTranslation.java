package ch.uzh.csg.reimbursement.model;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.springframework.transaction.annotation.Transactional;

import ch.uzh.csg.reimbursement.dto.CostCategoryTranslationDto;

@Entity
@Table(name = "CostCategoryTranslation_")
@Transactional
public class CostCategoryTranslation {

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

	@Setter
	@Column(nullable = false, updatable = false, unique = false, name = "type")
	@Enumerated(STRING)
	private CostCategoryType type;

	public CostCategoryTranslation(CostCategoryTranslationDto dto, CostCategoryType type) {
		setDe(dto.getDe());
		setEn(dto.getEn());
		setType(type);
	}

	/*
	 * The default constructor is needed by Hibernate, but should not be used at
	 * all.
	 */
	protected CostCategoryTranslation() {
	}
}
