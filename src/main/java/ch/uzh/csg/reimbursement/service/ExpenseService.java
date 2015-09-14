package ch.uzh.csg.reimbursement.service;

import static ch.uzh.csg.reimbursement.model.ExpenseState.ASSIGNED_TO_FINANCE_ADMIN;
import static ch.uzh.csg.reimbursement.model.ExpenseState.ASSIGNED_TO_PROFESSOR;
import static ch.uzh.csg.reimbursement.model.ExpenseState.DRAFT;
import static ch.uzh.csg.reimbursement.model.ExpenseState.REJECTED;

import java.util.Date;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.uzh.csg.reimbursement.dto.CreateExpenseDto;
import ch.uzh.csg.reimbursement.dto.ExpenseDto;
import ch.uzh.csg.reimbursement.model.Expense;
import ch.uzh.csg.reimbursement.model.ExpenseState;
import ch.uzh.csg.reimbursement.model.Role;
import ch.uzh.csg.reimbursement.model.User;
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

	public Expense create(CreateExpenseDto dto) {
		User user = userService.getLoggedInUser();

		Expense expense = new Expense(user, new Date(), null, dto.getAccounting(), DRAFT);
		expenseRepository.create(expense);

		return expense;
	}

	public Set<Expense> findAllByUser(String uid) {
		return expenseRepository.findAllByUser(uid);
	}

	public Set<Expense> findAllByAssignedManager() {
		String uid = userService.getLoggedInUser().getUid();
		return expenseRepository.findAllByAssignedManager(uid);
	}

	public Set<Expense> findAllByByState(ExpenseState state) {
		return expenseRepository.findAllByState(state);
	}

	public Set<Expense> findAllByCurrentUser() {
		User user = userService.getLoggedInUser();
		return findAllByUser(user.getUid());
	}

	public void updateExpense(String uid, ExpenseDto dto) {
		Expense expense = findByUid(uid);
		if (authorizationService.checkAuthorizationByState(expense)) {
			expense.setAccounting(dto.getAccounting());
		}
	}

	public Expense findByUid(String uid) {
		Expense expense = expenseRepository.findByUid(uid);

		if (expense == null) {
			LOG.debug("Expense not found in database with uid: " + uid);
			throw new ExpenseNotFoundException();
		}
		//TODO find better solution for authorization
		else if (authorizationService.checkAuthorizationByUser(expense)) {
			return expense;
		} else {
			return null;
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

	public void assignExpenseToProf(String uid) {
		Expense expense = findByUid(uid);
		User user = userService.getLoggedInUser();
		User financeAdmin = userService.findByUid("fadmin");
		if (authorizationService.checkAuthorizationByState(expense)) {
			if (user.getRoles().contains(Role.PROF)) {
				assignExpenseToFinanceAdmin(expense, financeAdmin);
			} else {
				expense.setAssignedManager(user.getManager());
				expense.setState(ASSIGNED_TO_PROFESSOR);
			}
		}
	}

	private void assignExpenseToFinanceAdmin(Expense expense, User financeAdmin) {
		expense.setFinanceAdmin(financeAdmin);
		assignExpenseToFinanceAdmin(expense.getUid());
	}

	public void assignExpenseToFinanceAdmin(String uid) {
		Expense expense = findByUid(uid);
		if (authorizationService.checkAuthorizationByState(expense)) {
			expense.setState(ASSIGNED_TO_FINANCE_ADMIN);
		}
	}

	public void rejectExpense(String uid) {
		Expense expense = findByUid(uid);
		if (authorizationService.checkAuthorizationByState(expense)) {
			expense.setState(REJECTED);
		}
	}

}
