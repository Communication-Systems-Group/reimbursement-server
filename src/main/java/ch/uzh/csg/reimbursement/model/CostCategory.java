package ch.uzh.csg.reimbursement.model;

import static javax.persistence.FetchType.EAGER;
import static javax.persistence.GenerationType.IDENTITY;

import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.springframework.transaction.annotation.Transactional;

import ch.uzh.csg.reimbursement.view.View;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@Table(name = "CostCategory")
@Transactional
@JsonIdentityInfo(
		generator = ObjectIdGenerators.PropertyGenerator.class,
		property = "uid")
public class CostCategory {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private int id;

	@JsonView(View.SummaryWithUid.class)
	@Getter
	@Column(nullable = false, updatable = true, unique = true, name = "uid")
	private String uid;

	@Getter
	@Setter
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "name_id")
	private CostCategoryName name;

	@Getter
	@Setter
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "description_id")
	private CostCategoryDescription description;

	@Getter
	@Setter
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "accounting_policy_id")
	private CostCategoryAccountingPolicy accountingPolicy;

	@Getter
	@Setter
	@OneToMany(mappedBy = "category", fetch = EAGER, cascade = CascadeType.ALL)
	private Set<Account> accounts;

	public CostCategory(CostCategoryName name, CostCategoryDescription description, CostCategoryAccountingPolicy accountingPolicy) {
		this.uid = UUID.randomUUID().toString();
		setName(name);
		setDescription(description);
		setAccountingPolicy(accountingPolicy);
	}

	public void updateCostCategory(CostCategoryName name, CostCategoryDescription description, CostCategoryAccountingPolicy accountingPolicy) {
		setName(name);
		setDescription(description);
		setAccountingPolicy(accountingPolicy);
	}

	/*
	 * The default constructor is needed by Hibernate, but should not be used at
	 * all.
	 */
	protected CostCategory() {
	}
}
