package ch.uzh.csg.reimbursement.model;

import static ch.uzh.csg.reimbursement.model.CostCategoryType.ACCOUNTING_POLICY;
import static ch.uzh.csg.reimbursement.model.CostCategoryType.DESCRIPTION;
import static ch.uzh.csg.reimbursement.model.CostCategoryType.NAME;
import static javax.persistence.GenerationType.IDENTITY;

import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Getter;

import org.springframework.transaction.annotation.Transactional;

import ch.uzh.csg.reimbursement.dto.CostCategoryDto;
import ch.uzh.csg.reimbursement.view.View.SummaryWithUid;

import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "CostCategory_")
@Transactional
public class CostCategory {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private int id;

	@JsonView(SummaryWithUid.class)
	@Getter
	@Column(nullable = false, updatable = true, unique = true, name = "uid")
	private String uid;

	@Getter
	@Column(nullable = false, updatable = true, unique = true, name = "account_number")
	private int accountNumber;

	@Getter
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "name_id")
	private CostCategoryTranslation name;

	@Getter
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "description_id")
	private CostCategoryTranslation description;

	@Getter
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "accounting_policy_id")
	private CostCategoryTranslation accountingPolicy;

	public CostCategory(CostCategoryDto dto) {
		this.uid = UUID.randomUUID().toString();
		this.accountNumber = dto.getAccountNumber();
		this.name = new CostCategoryTranslation(dto.getName(), NAME);
		this.description = new CostCategoryTranslation(dto.getDescription(), DESCRIPTION);
		this.accountingPolicy = new CostCategoryTranslation(dto.getAccountingPolicy(), ACCOUNTING_POLICY);
	}

	public void updateCostCategory(CostCategoryDto dto) {
		this.accountNumber = dto.getAccountNumber();
		this.name = new CostCategoryTranslation(dto.getName(), NAME);
		this.description = new CostCategoryTranslation(dto.getDescription(), DESCRIPTION);
		this.accountingPolicy = new CostCategoryTranslation(dto.getAccountingPolicy(), ACCOUNTING_POLICY);
	}

	/*
	 * The default constructor is needed by Hibernate, but should not be used at
	 * all.
	 */
	protected CostCategory() {
	}
}
