package ch.uzh.csg.reimbursement.model;

import static ch.uzh.csg.reimbursement.model.ExpenseState.ASSIGNED_TO_FINANCE_ADMIN;
import static ch.uzh.csg.reimbursement.model.ExpenseState.ASSIGNED_TO_MANAGER;
import static ch.uzh.csg.reimbursement.model.ExpenseState.DRAFT;
import static ch.uzh.csg.reimbursement.model.ExpenseState.PRINTED;
import static ch.uzh.csg.reimbursement.model.ExpenseState.REJECTED;
import static ch.uzh.csg.reimbursement.model.ExpenseState.SIGNED;
import static ch.uzh.csg.reimbursement.model.ExpenseState.TO_BE_ASSIGNED;
import static ch.uzh.csg.reimbursement.model.ExpenseState.TO_SIGN_BY_FINANCE_ADMIN;
import static ch.uzh.csg.reimbursement.model.ExpenseState.TO_SIGN_BY_MANAGER;
import static ch.uzh.csg.reimbursement.model.ExpenseState.TO_SIGN_BY_USER;
import static ch.uzh.csg.reimbursement.model.Role.DEPARTMENT_MANAGER;
import static ch.uzh.csg.reimbursement.model.Role.HEAD_OF_INSTITUTE;
import static java.util.UUID.randomUUID;
import static javax.persistence.CascadeType.ALL;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.GenerationType.IDENTITY;

import java.io.IOException;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import ch.uzh.csg.reimbursement.model.exception.ServiceException;
import ch.uzh.csg.reimbursement.model.exception.UnexpectedStateException;
import ch.uzh.csg.reimbursement.serializer.UserSerializer;
import ch.uzh.csg.reimbursement.view.View;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
@Table(name = "Expense_")
@Transactional
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "uid")
public class Expense {

	@Transient
	private final Logger LOG = LoggerFactory.getLogger(Expense.class);

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private int id;

	@JsonView(View.SummaryWithUid.class)
	@Getter
	@Column(nullable = false, updatable = true, unique = true, name = "uid")
	private String uid;

	@JsonView(View.DashboardSummary.class)
	@JsonSerialize(using = UserSerializer.class)
	@Getter
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@JsonView(View.DashboardSummary.class)
	public String getUserUid() {
		User user = getUser();
		if(user == null) {
			return null;
		}
		return user.getUid();
	}

	@JsonView(View.DashboardSummary.class)
	@Getter
	@Column(nullable = false, updatable = true, unique = false, name = "date")
	private Date date;

	@JsonView(View.DashboardSummary.class)
	@Getter
	@Enumerated(STRING)
	@Column(nullable = true, updatable = true, unique = false, name = "state")
	private ExpenseState state;

	@JsonView(View.Summary.class)
	@JsonSerialize(using = UserSerializer.class)
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "finance_admin_id")
	private User financeAdmin;

	@JsonView(View.DashboardSummary.class)
	public String getFinanceAdminUid() {
		User financeAdmin = getFinanceAdmin();
		if(financeAdmin == null) {
			return null;
		}
		return financeAdmin.getUid();
	}

	@JsonView(View.Summary.class)
	@JsonSerialize(using = UserSerializer.class)
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "assigned_manager_id")
	private User assignedManager;

	@JsonView(View.DashboardSummary.class)
	public String getAssignedManagerUid() {
		User assignedManager = getAssignedManager();
		if(assignedManager == null) {
			return null;
		}
		return assignedManager.getUid();
	}

	@JsonView(View.DashboardSummary.class)
	@Getter
	@Column(nullable = false, updatable = true, unique = false, name = "accounting")
	private String accounting;

	@JsonView(View.DashboardSummary.class)
	@Column(nullable = true, updatable = true, unique = false, name = "total_amount")
	private Double totalAmount = 0.0;

	@JsonView(View.Summary.class)
	@Getter
	@Column(nullable = true, updatable = true, unique = false, name = "comment")
	private String rejectComment;

	@Getter
	@OneToMany(mappedBy = "expense", fetch = EAGER, orphanRemoval = true)
	@OrderBy("date ASC")
	private Set<ExpenseItem> expenseItems;

	@OneToOne(cascade = ALL, orphanRemoval = true)
	@JoinColumn(name = "document_id")
	private Document expensePdf;

	@JsonView(View.Summary.class)
	@Getter
	@Setter
	@Column(nullable = false, updatable = true, columnDefinition = "boolean default true", name = "has_digital_signature")
	private Boolean hasDigitalSignature = true;

	public Expense(User user, User financeAdmin, String accounting) {
		this.user = user;
		this.date = new Date();
		setState(DRAFT);
		setFinanceAdmin(financeAdmin);
		setAccounting(accounting);
		this.uid = randomUUID().toString();
		LOG.debug("Expense constructor: Expense created");
	}

	public void updateExpense() {
		this.date = new Date();
		setTotalAmount();
		LOG.debug("Expense update method: Expense updated");
	}

	public Double getTotalAmount() {
		Double totalAmount = 0.0;
		for (ExpenseItem item : getExpenseItems()) {
			totalAmount += item.getCalculatedAmount();
		}
		this.totalAmount = totalAmount;
		return totalAmount;
	}

	public void setTotalAmount() {
		Double totalAmount = 0.0;
		if (getExpenseItems() != null) {
			for (ExpenseItem item : getExpenseItems()) {
				totalAmount += item.getCalculatedAmount();
			}
		}
		this.totalAmount = totalAmount;
		LOG.debug("Expense setTotalAmount method: Total amount set");
	}

	public Document setPdf(MultipartFile multipartFile) {

		byte[] content = null;
		try {
			content = multipartFile.getBytes();
			expensePdf.updateDocument(multipartFile.getContentType(), multipartFile.getSize(), content);
			LOG.debug("The expensePdf has been updated with a signedPdf");
			goToNextState();
		} catch (IOException e) {
			LOG.error("An IOException has been caught while creating a signature.", e);
			throw new ServiceException();
		}
		return expensePdf;
	}

	public void setPdf(Document document) {

		expensePdf = document;

		if (!this.hasDigitalSignature) {
			goToNextState();
		}
		LOG.debug("The expensePdf has been updated with a generatedPdf");
	}

	public Document getExpensePdf() {
		return expensePdf;
	}

	public void goToNextState() {

		if (state.equals(DRAFT) || state.equals(REJECTED)) {
			if(this.assignedManager.getRoles().contains(DEPARTMENT_MANAGER) || this.assignedManager.getRoles().contains(HEAD_OF_INSTITUTE)) {
				setState(TO_BE_ASSIGNED);
			} else {
				setState(ASSIGNED_TO_MANAGER);
			}
		} else if (state.equals(ASSIGNED_TO_MANAGER)) {
			setState(TO_BE_ASSIGNED);
		} else if (state.equals(TO_BE_ASSIGNED)) {
			setState(ASSIGNED_TO_FINANCE_ADMIN);
		} else if (state.equals(ASSIGNED_TO_FINANCE_ADMIN)) {
			setState(TO_SIGN_BY_USER);
		} else if (state.equals(TO_SIGN_BY_USER)) {
			setState(TO_SIGN_BY_MANAGER);
		} else if (state.equals(TO_SIGN_BY_MANAGER)) {
			setState(TO_SIGN_BY_FINANCE_ADMIN);
		} else if (state.equals(TO_SIGN_BY_FINANCE_ADMIN)) {
			setState(SIGNED);
		} else if (state.equals(SIGNED)) {
			setState(PRINTED);
		} else if (state.equals(PRINTED)) {
			setState(ASSIGNED_TO_FINANCE_ADMIN);
		} else {
			LOG.error("Unexpected State");
			throw new UnexpectedStateException();
		}
		LOG.debug("Expense goToNextState method: State set to state: " + this.state);
	}

	public void setAccounting(String accounting) {
		this.accounting = accounting;
		updateExpense();
	}

	public void reject(String comment) {
		setState(REJECTED);
		rejectComment = comment;
		LOG.debug("Expense reject method: Expense rejected");
	}

	private void setState(ExpenseState state) {
		this.state = state;
		updateExpense();
	}

	public User getCurrentEmailReceiverBasedOnExpenseState(){
		User user;
		switch (this.getState()) {
		case ASSIGNED_TO_MANAGER:
		case TO_SIGN_BY_MANAGER:
			user = this.getAssignedManager();
			break;
		case ASSIGNED_TO_FINANCE_ADMIN:
		case TO_SIGN_BY_FINANCE_ADMIN:
			user = this.getFinanceAdmin();
			break;
		case TO_SIGN_BY_USER:
		default:
			user = this.getUser();
			break;
		}
		return user;
	}

	/*
	 * The default constructor is needed by Hibernate, but should not be used at
	 * all.
	 */
	protected Expense() {
	}
}
