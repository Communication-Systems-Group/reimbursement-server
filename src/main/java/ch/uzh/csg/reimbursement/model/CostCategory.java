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
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
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

	@Getter
	@Column(nullable = false, updatable = true, unique = true, name = "uid")
	private String uid;

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
	@Setter
	@OneToMany(mappedBy = "category", fetch = EAGER, cascade = CascadeType.ALL)
	private Set<Account> accounts;

	public CostCategory(String name, String description, String accountingPolicy) {
		setName(name);
		setDescription(description);
		setAccountingPolicy(accountingPolicy);
		this.uid = UUID.randomUUID().toString();
	}

	/*
	 * The default constructor is needed by Hibernate, but should not be used at all.
	 */
	protected CostCategory() {
	}
}
