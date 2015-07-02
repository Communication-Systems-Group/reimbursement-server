package ch.uzh.csg.reimbursement.model;

import static javax.persistence.FetchType.EAGER;
import static javax.persistence.GenerationType.IDENTITY;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.springframework.transaction.annotation.Transactional;

@Entity
@Table(name = "CostCategory")
@Transactional
public class CostCategory {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private int id;

	@Getter
	@Setter
	@Column(nullable = false, updatable = false, unique = false, name = "name")
	private String name;

	@Getter
	@Setter
	@Column(nullable = false, updatable = true, unique = false, name = "description")
	private String description;

	@Getter
	@Setter
	@Column(nullable = false, updatable = true, unique = false, name = "accounting_policy")
	private String accountingPolicy;

	@Getter
	@OneToMany(mappedBy = "category", fetch = EAGER, cascade = CascadeType.ALL)
	private Set<Account> accounts = new HashSet<Account>();

	public CostCategory(String name, String description, String accountingPolicy) {
		setName(name);
		setDescription(description);
		setAccountingPolicy(accountingPolicy);
	}

	/*
	 * The default constructor is needed by Hibernate, but should not be used at all.
	 */
	protected CostCategory() {
	}
}
