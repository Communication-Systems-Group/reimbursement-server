package ch.uzh.csg.reimbursement.service;

import static ch.uzh.csg.reimbursement.model.ExpenseState.ASSIGNED_TO_FINANCE_ADMIN;
import static ch.uzh.csg.reimbursement.model.ExpenseState.ASSIGNED_TO_PROF;
import static ch.uzh.csg.reimbursement.model.ExpenseState.DRAFT;
import static ch.uzh.csg.reimbursement.model.ExpenseState.PRINTED;
import static ch.uzh.csg.reimbursement.model.ExpenseState.REJECTED;
import static ch.uzh.csg.reimbursement.model.ExpenseState.SIGNED;
import static ch.uzh.csg.reimbursement.model.ExpenseState.TO_BE_ASSIGNED;
import static ch.uzh.csg.reimbursement.model.ExpenseState.TO_SIGN_BY_PROF;
import static ch.uzh.csg.reimbursement.model.Role.PROF;
import static ch.uzh.csg.reimbursement.model.Role.USER;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import ch.uzh.csg.reimbursement.dto.AccessRights;
import ch.uzh.csg.reimbursement.dto.ExpenseStateStatisticsDto;
import ch.uzh.csg.reimbursement.dto.SearchExpenseDto;
import ch.uzh.csg.reimbursement.model.Document;
import ch.uzh.csg.reimbursement.model.Expense;
import ch.uzh.csg.reimbursement.model.ExpenseState;
import ch.uzh.csg.reimbursement.model.Role;
import ch.uzh.csg.reimbursement.model.Token;
import ch.uzh.csg.reimbursement.model.User;
import ch.uzh.csg.reimbursement.model.exception.AccessViolationException;
import ch.uzh.csg.reimbursement.model.exception.AssignViolationException;
import ch.uzh.csg.reimbursement.model.exception.ExpenseDeleteViolationException;
import ch.uzh.csg.reimbursement.model.exception.ExpenseNotFoundException;
import ch.uzh.csg.reimbursement.model.exception.PdfExportViolationException;
import ch.uzh.csg.reimbursement.model.exception.TokenNotFoundException;
import ch.uzh.csg.reimbursement.repository.ExpenseRepositoryProvider;

@Service
@Transactional
public class ExpenseService {

	private final Logger LOG = LoggerFactory.getLogger(ExpenseService.class);

	@Autowired
	private ExpenseRepositoryProvider expenseRepository;

	@Autowired
	private UserResourceAuthorizationService authorizationService;

	@Autowired
	private UserService userService;

	@Autowired
	private TokenService tokenService;

	@Autowired
	private PdfGenerationService pdfGenerationService;

	@Value("${reimbursement.token.epxenseItemAttachmentMobile.expirationInMilliseconds}")
	private int tokenExpirationInMilliseconds;

	public Expense createExpense(String accounting) {
		User user = userService.getLoggedInUser();
		Expense expense = new Expense(user, null, accounting);
		expenseRepository.create(expense);

		return expense;
	}

	public Set<Expense> getAllByUser(String uid) {
		return expenseRepository.findAllByUser(uid);
	}

	public Set<Expense> getAllReviewExpenses() {
		User user = userService.getLoggedInUser();

		if (user.getRoles().contains(PROF)) {
			return getAllByAssignedManager(user);
		} else {
			return getAllForFinanceAdmin(user);
		}
	}

	public Set<Expense> getAllByAssignedManager(User user) {
		return expenseRepository.findAllByAssignedManager(user);
	}

	public Set<Expense> getAllForFinanceAdmin(User user) {
		Set<Expense> expenses;
		// Get the review expenses for the finance admin
		// For finance admin all expenses have to be shown that are in the state
		// TO_BE_ASSIGNED
		expenses = expenseRepository.findAllByState(TO_BE_ASSIGNED, user);
		// In addition to that the expenses that are assigned to the finance
		// admin have to be shown
		expenses.addAll(expenseRepository.findAllByFinanceAdmin(user));

		return expenses;
	}

	public Set<Expense> getAllByCurrentUser() {
		User user = userService.getLoggedInUser();
		return getAllByUser(user.getUid());
	}

	public void updateExpense(String uid, String accounting) {
		Expense expense = getByUid(uid);

		if (authorizationService.checkEditAuthorization(expense)) {
			expense.setAccounting(accounting);
		} else {
			LOG.debug("The logged in user has no access to this expense");
			throw new AccessViolationException();
		}
	}

	public Expense getByUid(String uid) {

		if (userService.userIsLoggedIn()) {
			return getByExpenseUid(uid);
		} else {
			return getByTokenUid(uid);
		}
	}

	private Expense getByExpenseUid(String uid) {
		Expense expense = expenseRepository.findByUid(uid);

		if (expense != null) {
			if (authorizationService.checkViewAuthorization(expense)) {
				return expense;
			} else {
				LOG.debug("The logged in user has no access to this expense");
				throw new AccessViolationException();
			}
		} else {
			LOG.debug("Expense not found in database with uid: " + uid);
			throw new ExpenseNotFoundException();
		}
	}

	private Expense getByTokenUid(String tokenUid) {
		Token token = tokenService.getByUid(tokenUid);
		Expense expense = null;

		if (token != null) {
			expense = expenseRepository.findByUid(token.getContent());
		} else {
			LOG.debug("The token has no access to this expense");
			throw new TokenNotFoundException();
		}

		if (expense != null) {
			if (authorizationService.checkViewAuthorizationMobile(expense, token)) {
				return expense;
			} else {
				LOG.debug("The token has no access to this expense");
				throw new AccessViolationException();
			}
		} else {
			LOG.debug("Expense not found in database with uid: " + tokenUid);
			throw new ExpenseNotFoundException();
		}
	}

	public void deleteExpense(String uid) {
		Expense expense = getByUid(uid);

		if (expense.getState() == DRAFT || expense.getState() == REJECTED) {
			expenseRepository.delete(expense);
		} else {
			LOG.debug("Expense cannot be deleted in this state");
			throw new ExpenseDeleteViolationException();
		}
	}

	public void acceptExpense(String uid) {
		Expense expense = getByUid(uid);

		if (authorizationService.checkEditAuthorization(expense)) {
			if (authorizationService.checkAssignAuthorization(expense)) {
				expense.goToNextState();
			} else {
				LOG.debug("Expenses without expenseItems cannot be assigned.");
				throw new AssignViolationException();
			}
		} else {
			LOG.debug("The logged in user has no access to this expense");
			throw new AccessViolationException();
		}
	}

	public void assignExpenseToMe(String uid) {
		Expense expense = getByUid(uid);
		User user = userService.getLoggedInUser();

		if (authorizationService.checkEditAuthorization(expense)) {
			if (user != expense.getUser()) {
				expense.setFinanceAdmin(user);
				expense.goToNextState();
			} else {
				LOG.debug("The logged in user has no access to this expense");
				throw new AssignViolationException();
			}
		} else {
			LOG.debug("The logged in user has no access to this expense");
			throw new AccessViolationException();
		}
	}

	public void assignExpenseToProf(String uid) {
		Expense expense = getByUid(uid);
		User user = userService.getLoggedInUser();
		User financeAdmin = userService.getByUid("fadmin");

		if (authorizationService.checkEditAuthorization(expense)) {
			if (authorizationService.checkAssignAuthorization(expense)) {
				// If the prof wants to hand in an expense the expense is
				// directly assigned to the chief of finance_admins
				if (user.getRoles().contains(Role.PROF)) {
					// TODO The department manager should be set in the
					// application properties
					expense.setFinanceAdmin(financeAdmin);
					User manager = userService.getByUid("lauber");
					expense.setAssignedManager(manager);
					expense.goToNextState();
				} else {
					expense.setAssignedManager(user.getManager());
					expense.goToNextState();
				}
			} else {
				LOG.debug("Expenses without expenseItems cannot be assigned.");
				throw new AssignViolationException();
			}
		} else {
			LOG.debug("The logged in user has no access to this expense");
			throw new AccessViolationException();
		}
	}

	public void rejectExpense(String uid, String comment) {
		Expense expense = getByUid(uid);

		if (authorizationService.checkEditAuthorization(expense)) {
			expense.reject(comment);
		} else {
			LOG.debug("The logged in user has no access to this expense");
			throw new AccessViolationException();
		}
	}

	public AccessRights getAccessRights(String uid) {
		AccessRights rights = new AccessRights();
		Expense expense = null;

		try {
			expense = getByUid(uid);
			rights.setViewable(true);

			if (userService.userIsLoggedIn()) {
				rights = setEditRights(expense, rights);
				rights = setSignRights(expense, rights);
			} else {
				Token token = tokenService.getByUid(uid);
				if (token != null) {
					rights = setEditRightsMobile(expense, rights, token);
					rights = setSignRightsMobile(expense, rights, token);
				} else {
					rights.setEditable(false);
					rights.setSignable(false);
				}
			}
		} catch (AccessViolationException e) {
		}
		return rights;
	}

	private AccessRights setEditRights(Expense expense, AccessRights rights) {
		if (authorizationService.checkEditAuthorization(expense)) {
			rights.setEditable(true);
		} else {
			rights.setEditable(false);
		}
		return rights;
	}

	private AccessRights setSignRights(Expense expense, AccessRights rights) {
		if (authorizationService.checkSignAuthorization(expense)) {
			rights.setSignable(true);
		} else {
			rights.setSignable(false);
		}
		return rights;
	}

	private AccessRights setEditRightsMobile(Expense expense, AccessRights rights, Token token) {
		if (authorizationService.checkEditAuthorizationMobile(expense, token)) {
			rights.setEditable(true);
		} else {
			rights.setEditable(false);
		}
		return rights;
	}

	private AccessRights setSignRightsMobile(Expense expense, AccessRights rights, Token token) {
		if (authorizationService.checkSignAuthorizationMobile(expense, token)) {
			rights.setSignable(true);
		} else {
			rights.setSignable(false);
		}
		return rights;
	}

	public Set<Expense> search(SearchExpenseDto dto) {
		String accountingText = "%";
		Date startTime = null;
		Date endTime = null;

		if (dto.getAccountingText() != null && !dto.getAccountingText().equals("")) {
			accountingText = "%" + dto.getAccountingText() + "%";
		}


		if (dto.getStartTime() != null) {
			startTime = dto.getStartTime();
		}

		if (dto.getEndTime() != null) {
			endTime = dto.getEndTime();
		}

		List<User> relevantUsers = new ArrayList<>();

		// search for the last name
		List<User> temporaryUsers;

		if (dto.getLastName() != null && !dto.getLastName().equals("")) {
			temporaryUsers = userService.getAllByLastName("%" + dto.getLastName() + "%");
		} else {
			temporaryUsers = userService.getAll();
		}

		// filter for the role
		if (dto.getRole() != null && !dto.getRole().equals("")) {
			Role role = null;
			try {
				role = Role.valueOf(dto.getRole());
			} catch (IllegalArgumentException e) {
				LOG.debug("Illegal role name, ignoring.");
			}
			if (role != null) {
				for (User user : temporaryUsers) {
					Set<Role> roles = user.getRoles();
					if (role == USER) {
						// if role is user, only the users and not admin/fadmin
						// etc are added
						if (roles.contains(role) && roles.size() == 1) {
							relevantUsers.add(user);
						}
					} else {
						if (roles.contains(role)) {
							relevantUsers.add(user);
						}
					}
				}
			}
		} else {
			relevantUsers = temporaryUsers;
		}

		return expenseRepository.search(relevantUsers, accountingText, startTime, endTime);
	}

	public Document setSignedPdf(String expenseUid, MultipartFile multipartFile) {
		Expense expense = getByUid(expenseUid);
		return expense.setPdf(multipartFile);
	}

	public Document getPdf(String uid) {
		Expense expense = getByUid(uid);

		if (expense.getExpensePdf() == null) {
			LOG.debug("The PDF for the expense has not been generated yet");
			throw new PdfExportViolationException();
		} else {
			return expense.getExpensePdf();
		}
	}

	public void generatePdf(String uid, String url) {
		Expense expense = getByUid(uid);

		String tokenUid = tokenService.createUniAdminToken(uid);
		String urlWithTokenUid = url + tokenUid;

		expense.setPdf(pdfGenerationService.generatePdf(expense, urlWithTokenUid));
	}

	public ExpenseStateStatisticsDto getExpenseStateStatistics() {
		ExpenseStateStatisticsDto dto = new ExpenseStateStatisticsDto();

		dto.setTotalAmountOfExpenses(expenseRepository.countExpenses());
		dto.setDraft(expenseRepository.countByState(DRAFT));
		dto.setAssignedToProf(expenseRepository.countByState(ASSIGNED_TO_PROF));
		dto.setRejected(expenseRepository.countByState(REJECTED));
		dto.setToBeAssigned(expenseRepository.countByState(TO_BE_ASSIGNED));
		dto.setAssignedToFinanceAdmin(expenseRepository.countByState(ASSIGNED_TO_FINANCE_ADMIN));
		dto.setToSignByUser(expenseRepository.countByState(ExpenseState.TO_SIGN_BY_USER));
		dto.setToSignByProf(expenseRepository.countByState(TO_SIGN_BY_PROF));
		dto.setToSignByFinanceAdmin(expenseRepository.countByState(ExpenseState.TO_SIGN_BY_FINANCE_ADMIN));
		dto.setSigned(expenseRepository.countByState(SIGNED));
		dto.setPrinted(expenseRepository.countByState(PRINTED));

		return dto;
	}

	public void digitalSignature(String uid, Boolean hasDigitalSignature) {
		Expense expense = getByUid(uid);
		if (authorizationService.checkDigitalSignatureDecision(expense)) {
			expense.setHasDigitalSignature(hasDigitalSignature);
		} else {
			LOG.debug("The logged in user has no access to this expense");
			throw new AccessViolationException();
		}
	}
}
