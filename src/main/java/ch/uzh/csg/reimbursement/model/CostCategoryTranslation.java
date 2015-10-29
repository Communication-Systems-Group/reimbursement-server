package ch.uzh.csg.reimbursement.model;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import ch.uzh.csg.reimbursement.dto.CostCategoryTranslationDto;

@Entity
@Table(name = "CostCategoryTranslation_")
@Transactional
public class CostCategoryTranslation {

	@Transient
	private final Logger LOG = LoggerFactory.getLogger(CostCategoryTranslation.class);

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private int id;

	@Getter
	@Column(nullable = true, updatable = true, unique = false, name = "de")
	private String de;

	@Getter
	@Column(nullable = true, updatable = true, unique = false, name = "en")
	private String en;

	@Column(nullable = false, updatable = false, unique = false, name = "type")
	@Enumerated(STRING)
	private CostCategoryType type;

	public CostCategoryTranslation(CostCategoryTranslationDto dto, CostCategoryType type) {
		this.de = dto.getDe();
		this.en = dto.getEn();
		this.type = type;
		LOG.debug("CostCategoryTranslation constructor: CostCategoryTranslation created");
	}

	/*
	 * The default constructor is needed by Hibernate, but should not be used at
	 * all.
	 */
	protected CostCategoryTranslation() {
	}
}
