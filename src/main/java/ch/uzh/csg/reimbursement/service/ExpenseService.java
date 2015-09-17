package ch.uzh.csg.reimbursement.service;

import static ch.uzh.csg.reimbursement.model.ExpenseState.ASSIGNED_TO_FINANCE_ADMIN;
import static ch.uzh.csg.reimbursement.model.ExpenseState.ASSIGNED_TO_PROF;
import static ch.uzh.csg.reimbursement.model.ExpenseState.DRAFT;
import static ch.uzh.csg.reimbursement.model.ExpenseState.REJECTED;
import static ch.uzh.csg.reimbursement.model.ExpenseState.TO_SIGN_BY_USER;
import static ch.uzh.csg.reimbursement.model.Role.FINANCE_ADMIN;
import static ch.uzh.csg.reimbursement.model.Role.PROF;

import java.util.Date;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.uzh.csg.reimbursement.dto.AccessRights;
import ch.uzh.csg.reimbursement.dto.CommentDto;
import ch.uzh.csg.reimbursement.dto.CreateExpenseDto;
import ch.uzh.csg.reimbursement.dto.ExpenseDto;
import ch.uzh.csg.reimbursement.dto.SearchDto;
import ch.uzh.csg.reimbursement.model.Expense;
import ch.uzh.csg.reimbursement.model.ExpenseState;
import ch.uzh.csg.reimbursement.model.Role;
import ch.uzh.csg.reimbursement.model.User;
import ch.uzh.csg.reimbursement.model.exception.AccessViolationException;
import ch.uzh.csg.reimbursement.model.exception.ExpenseDeleteViolationException;
import ch.uzh.csg.reimbursement.model.exception.ExpenseNotFoundException;
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
	private CommentService commentService;

	@Autowired
	private CostCategoryService costCategoryService;

	public Expense create(CreateExpenseDto dto) {
		User user = userService.getLoggedInUser();

		Expense expense = new Expense(user, new Date(), null, dto.getAccounting(), DRAFT);
		expenseRepository.create(expense);

		return expense;
	}

	public Set<Expense> findAllByUser(String uid) {
		return expenseRepository.findAllByUser(uid);
	}

	public Set<Expense> findAllByAssignedManager(User user) {
		return expenseRepository.findAllByAssignedManager(user.getUid());
	}

	public Set<Expense> findAllByByState(ExpenseState state) {
		return expenseRepository.findAllByState(state);
	}

	public Set<Expense> getAllReviewExpenses() {
		User user = userService.getLoggedInUser();
		if (user.getRoles().contains(PROF)) {
			return findAllByAssignedManager(user);
		} else if (user.getRoles().contains(FINANCE_ADMIN)) {
			return findAllByByState(ASSIGNED_TO_FINANCE_ADMIN);
		} else {
			LOG.debug("The logged in user has no access to this expense");
			throw new AccessViolationException();
		}
	}

	public Set<Expense> findAllByCurrentUser() {
		User user = userService.getLoggedInUser();
		return findAllByUser(user.getUid());
	}

	public void updateExpense(String uid, ExpenseDto dto) {
		Expense expense = findByUid(uid);
		if (authorizationService.checkAuthorizationByState(expense)) {
			expense.setAccounting(dto.getAccounting());
		} else {
			LOG.debug("The logged in user has no access to this expense");
			throw new AccessViolationException();
		}
	}

	public Expense findByUid(String uid) {
		Expense expense = expenseRepository.findByUid(uid);

		if (expense == null) {
			LOG.debug("Expense not found in database with uid: " + uid);
			throw new ExpenseNotFoundException();
		}
		// TODO find better solution for authorization
		else if (authorizationService.checkAuthorizationByUser(expense)) {
			return expense;
		} else {
			LOG.debug("The logged in user has no access to this expense");
			throw new AccessViolationException();
		}
	}

	public void delete(String uid) {
		Expense expense = findByUid(uid);
		if (expense.getState() == DRAFT) {
			expenseRepository.delete(expense);
		} else {
			LOG.debug("Expense cannot be deleted in this state");
			throw new ExpenseDeleteViolationException();
		}
	}

	public void acceptExpense(String uid) {
		Expense expense = findByUid(uid);
		if (expense.getState().equals(ASSIGNED_TO_PROF)) {
			assignExpenseToFinanceAdmin(expense);
		} else {
			expense.setState(TO_SIGN_BY_USER);
		}
	}

	public void assignExpenseToMe(String uid) {
		Expense expense = findByUid(uid);
		User user = userService.getLoggedInUser();
		if (authorizationService.checkAuthorizationByState(expense)) {
			expense.setFinanceAdmin(user);
		} else {
			LOG.debug("The logged in user has no access to this expense");
			throw new AccessViolationException();
		}
	}

	public void assignExpenseToProf(String uid) {
		Expense expense = findByUid(uid);
		User user = userService.getLoggedInUser();
		User financeAdmin = userService.findByUid("fadmin");
		if (authorizationService.checkAuthorizationByState(expense)) {
			if (user.getRoles().contains(Role.PROF)) {
				assignExpenseToFinanceAdmin(expense, financeAdmin);
				// TODO The department manager should be set in the application
				// properties
				User manager = userService.findByUid("lauber");
				expense.setAssignedManager(manager);
			} else {
				expense.setAssignedManager(user.getManager());
				expense.setState(ASSIGNED_TO_PROF);
			}
		} else {
			LOG.debug("The logged in user has no access to this expense");
			throw new AccessViolationException();
		}
	}

	private void assignExpenseToFinanceAdmin(Expense expense, User financeAdmin) {
		expense.setFinanceAdmin(financeAdmin);
		assignExpenseToFinanceAdmin(expense);
	}

	public void assignExpenseToFinanceAdmin(Expense expense) {
		if (authorizationService.checkAuthorizationByState(expense)) {
			expense.setState(ASSIGNED_TO_FINANCE_ADMIN);
		} else {
			LOG.debug("The logged in user has no access to this expense");
			throw new AccessViolationException();
		}
	}

	public void rejectExpense(String uid, CommentDto dto) {
		Expense expense = findByUid(uid);
		if (authorizationService.checkAuthorizationByState(expense)) {
			expense.setState(REJECTED);
			commentService.createExpenseComment(expense, dto);
		} else {
			LOG.debug("The logged in user has no access to this expense");
			throw new AccessViolationException();
		}
	}

	public AccessRights getAccessRights(String uid) {
		AccessRights rights = new AccessRights();
		Expense expense;
		try {
			expense = findByUid(uid);
			rights.setViewable(true);

			if (authorizationService.checkAuthorizationByState(expense)) {
				rights.setEditable(true);
			} else {
				rights.setEditable(false);
			}

			if (authorizationService.checkSignAuthorization(expense)) {
				rights.setSignable(true);
			} else {
				rights.setSignable(false);
			}

		} catch (AccessViolationException e) {
			rights.setViewable(false);
			rights.setEditable(false);
			rights.setSignable(false);
		}

		return rights;
	}

	public Set<Expense> getExpensesForAdminPool(SearchDto dto) {
		return expenseRepository.findExpensesForAdminPool(dto);
	}
}
