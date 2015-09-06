package ch.uzh.csg.reimbursement.service;

import static ch.uzh.csg.reimbursement.model.ExpenseState.ASSIGNED_TO_FINANCE_ADMIN;
import static ch.uzh.csg.reimbursement.model.ExpenseState.DRAFT;
import static ch.uzh.csg.reimbursement.model.ExpenseState.REJECTED;
import static ch.uzh.csg.reimbursement.model.Role.FINANCE_ADMIN;

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
import ch.uzh.csg.reimbursement.model.User;
import ch.uzh.csg.reimbursement.model.exception.ExpenseAccessViolationException;
import ch.uzh.csg.reimbursement.model.exception.ExpenseDeleteViolationException;
import ch.uzh.csg.reimbursement.model.exception.ExpenseNotFoundException;
import ch.uzh.csg.reimbursement.repository.ExpenseRepositoryProvider;
import ch.uzh.csg.reimbursement.view.ExpenseResourceMapper;

@Service
@Transactional
public class ExpenseService {

	private final Logger LOG = LoggerFactory.getLogger(ExpenseService.class);

	@Autowired
	private ExpenseRepositoryProvider expenseRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private ExpenseResourceMapper expenseResourceMapper;

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
		User financeAdmin = null;
		if (userService.getLoggedInUser().getRoles().contains(FINANCE_ADMIN)
				&& expense.getUser() != userService.getLoggedInUser()) {
			financeAdmin = userService.getLoggedInUser();
		}
		User assignedManager;
		if (dto.getState() == ASSIGNED_TO_FINANCE_ADMIN) {
			assignedManager = null;
		} else {
			assignedManager = userService.findByUid(dto.getAssignedManagerUid());
		}
		expense.updateExpense(new Date(), financeAdmin, dto.getAccounting(), assignedManager, dto.getState());
	}

	public Expense findByUid(String uid) {
		Expense expense = expenseRepository.findByUid(uid);

		if (expense == null) {
			LOG.debug("Expense not found in database with uid: " + uid);
			throw new ExpenseNotFoundException();
		} else if ((expense.getState() == DRAFT || expense.getState() == REJECTED)
				&& expense.getUser() != userService.getLoggedInUser()) {
			LOG.debug("The logged in user has no access to this expense");
			throw new ExpenseAccessViolationException();
		} else if ((expense.getState() != DRAFT && expense.getState() != REJECTED &&
				expense.getState() != ASSIGNED_TO_FINANCE_ADMIN)
				&& expense.getAssignedManager() != userService.getLoggedInUser()) {
			LOG.debug("Expense not assigned to logged in user");
			throw new ExpenseAccessViolationException();
		} else if (expense.getState() == ASSIGNED_TO_FINANCE_ADMIN
				&& !(userService.getLoggedInUser().getRoles().contains(FINANCE_ADMIN))
				&& expense.getUser() == userService.getLoggedInUser()) {
			LOG.debug("The logged in user has no access to this expense");
			throw new ExpenseAccessViolationException();
		}
		return expense;
	}

	public void delete(String uid) {
		Expense expense = expenseRepository.findByUid(uid);
		if(expense.getState() == DRAFT) {
			expenseRepository.delete(expense);
		}
		else {
			LOG.debug("Expense cannot be deleted in this state");
			throw new ExpenseDeleteViolationException();
		}
	}
}
