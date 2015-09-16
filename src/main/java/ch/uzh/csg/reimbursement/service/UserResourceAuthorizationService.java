package ch.uzh.csg.reimbursement.service;

import static ch.uzh.csg.reimbursement.model.ExpenseState.ACCEPTED;
import static ch.uzh.csg.reimbursement.model.ExpenseState.ASSIGNED_TO_FINANCE_ADMIN;
import static ch.uzh.csg.reimbursement.model.ExpenseState.ASSIGNED_TO_PROFESSOR;
import static ch.uzh.csg.reimbursement.model.ExpenseState.DRAFT;
import static ch.uzh.csg.reimbursement.model.ExpenseState.REJECTED;
import static ch.uzh.csg.reimbursement.model.ExpenseState.SIGNED_BY_PROF;
import static ch.uzh.csg.reimbursement.model.ExpenseState.SIGNED_BY_USER;
import static ch.uzh.csg.reimbursement.model.Role.FINANCE_ADMIN;
import static ch.uzh.csg.reimbursement.model.Role.PROF;
import static ch.uzh.csg.reimbursement.model.Role.USER;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.uzh.csg.reimbursement.model.Expense;
import ch.uzh.csg.reimbursement.model.ExpenseItem;

@Service
@Transactional
public class UserResourceAuthorizationService {

	@Autowired
	private UserService userService;

	@Autowired
	private ExpenseService expenseService;

	public boolean checkAuthorizationByState(Expense expense) {

		if ((expense.getState().equals(DRAFT) || expense.getState().equals(REJECTED))
				&& expense.getUser().equals(userService.getLoggedInUser())) {
			return true;
		} else if (checkAuthorizationForProfAndFinanceAdmin(expense)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean checkAuthorizationByState(ExpenseItem expenseItem) {

		Expense expense = expenseItem.getExpense();

		return checkAuthorizationByState(expense);
	}

	public boolean checkAuthorizationByUser(Expense expense) {

		if (expense.getUser().equals(userService.getLoggedInUser())) {
			return true;
		} else if (checkAuthorizationForProfAndFinanceAdmin(expense)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean checkAuthorizationByUser(ExpenseItem expenseItem) {
		Expense expense = expenseItem.getExpense();

		return checkAuthorizationByUser(expense);
	}

	private boolean checkAuthorizationForProfAndFinanceAdmin(Expense expense) {
		if (expense.getState().equals(ASSIGNED_TO_PROFESSOR) && expense.getAssignedManager() != null
				&& expense.getAssignedManager().equals(userService.getLoggedInUser())) {
			return true;
		} else if (expense.getState().equals(ASSIGNED_TO_FINANCE_ADMIN)
				&& userService.getLoggedInUser().getRoles().contains(FINANCE_ADMIN)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean checkSignAuthorization(Expense expense) {
		if(expense.getState().equals(ACCEPTED) && userService.getLoggedInUser().getRoles().contains(USER)) {
			return true;
		} else if(expense.getState().equals(SIGNED_BY_USER) && userService.getLoggedInUser().getRoles().contains(PROF)) {
			return true;
		} else if(expense.getState().equals(SIGNED_BY_PROF) && userService.getLoggedInUser().getRoles().contains(FINANCE_ADMIN)) {
			return true;
		} else {
			return false;
		}
	}
}