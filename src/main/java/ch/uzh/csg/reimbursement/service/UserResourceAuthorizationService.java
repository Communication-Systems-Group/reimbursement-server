package ch.uzh.csg.reimbursement.service;

import static ch.uzh.csg.reimbursement.model.ExpenseState.ASSIGNED_TO_FINANCE_ADMIN;
import static ch.uzh.csg.reimbursement.model.ExpenseState.ASSIGNED_TO_PROFESSOR;
import static ch.uzh.csg.reimbursement.model.ExpenseState.DRAFT;
import static ch.uzh.csg.reimbursement.model.ExpenseState.REJECTED;
import static ch.uzh.csg.reimbursement.model.Role.FINANCE_ADMIN;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.uzh.csg.reimbursement.model.Expense;
import ch.uzh.csg.reimbursement.model.ExpenseItem;
import ch.uzh.csg.reimbursement.model.exception.AccessViolationException;

@Service
@Transactional
public class UserResourceAuthorizationService {

	private final Logger LOG = LoggerFactory.getLogger(UserResourceAuthorizationService.class);

	@Autowired
	private UserService userService;

	@Autowired
	private UserService expenseService;

	public boolean checkAuthorization(Expense expense) {

		if ((expense.getState().equals(DRAFT) || expense.getState().equals(REJECTED))
				&& !expense.getUser().equals(userService.getLoggedInUser())) {
			LOG.debug("The logged in user has no access to this expense");
			throw new AccessViolationException();
		} else if (expense.getState().equals(ASSIGNED_TO_PROFESSOR) && !expense.getAssignedManager().equals(
				userService.getLoggedInUser())) {
			LOG.debug("The logged in user has no access to this expense");
			throw new AccessViolationException();
		} else if (expense.getState().equals(ASSIGNED_TO_FINANCE_ADMIN)
				&& !userService.getLoggedInUser().getRoles().contains(FINANCE_ADMIN)) {
			LOG.debug("The logged in user has no access to this expense");
			throw new AccessViolationException();
		}
		return true;
	}

	public boolean checkAuthorization(ExpenseItem expenseItem) {

		Expense expense = expenseItem.getExpense();

		return checkAuthorization(expense);
	}
}