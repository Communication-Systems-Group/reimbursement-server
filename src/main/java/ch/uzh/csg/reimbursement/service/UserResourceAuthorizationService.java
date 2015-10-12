package ch.uzh.csg.reimbursement.service;

import static ch.uzh.csg.reimbursement.model.ExpenseState.ASSIGNED_TO_PROF;
import static ch.uzh.csg.reimbursement.model.ExpenseState.DRAFT;
import static ch.uzh.csg.reimbursement.model.ExpenseState.PRINTED;
import static ch.uzh.csg.reimbursement.model.ExpenseState.REJECTED;
import static ch.uzh.csg.reimbursement.model.ExpenseState.TO_BE_ASSIGNED;
import static ch.uzh.csg.reimbursement.model.ExpenseState.TO_SIGN_BY_FINANCE_ADMIN;
import static ch.uzh.csg.reimbursement.model.ExpenseState.TO_SIGN_BY_PROF;
import static ch.uzh.csg.reimbursement.model.ExpenseState.TO_SIGN_BY_USER;
import static ch.uzh.csg.reimbursement.model.Role.FINANCE_ADMIN;
import static ch.uzh.csg.reimbursement.model.Role.PROF;
import static ch.uzh.csg.reimbursement.model.Role.UNI_ADMIN;
import static ch.uzh.csg.reimbursement.model.Role.USER;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.uzh.csg.reimbursement.model.Expense;
import ch.uzh.csg.reimbursement.model.ExpenseItem;
import ch.uzh.csg.reimbursement.model.Token;
import ch.uzh.csg.reimbursement.model.User;

@Service
@Transactional
public class UserResourceAuthorizationService {

	@Autowired
	private UserService userService;

	@Autowired
	private ExpenseService expenseService;

	public boolean checkEditAuthorization(Expense expense) {
		return checkEditAuthorization(expense, userService.getLoggedInUser());
	}

	public boolean checkEditAuthorization(ExpenseItem expenseItem) {
		return checkEditAuthorization(expenseItem.getExpense());
	}

	public boolean checkEditAuthorizationMobile(Expense expense, Token token) {
		return checkEditAuthorization(expense, token.getUser());
	}

	private boolean checkEditAuthorization(Expense expense, User user) {
		if ((expense.getState().equals(DRAFT) || expense.getState().equals(REJECTED) || expense.getState().equals(
				PRINTED))
				&& expense.getUser().equals(user)) {
			return true;
		} else if (expense.getState().equals(ASSIGNED_TO_PROF) && expense.getAssignedManager() != null
				&& expense.getAssignedManager().equals(user)) {
			return true;
		} else if ((expense.getState().equals(TO_BE_ASSIGNED) && user.getRoles().contains(FINANCE_ADMIN) && user != expense
				.getUser()) || (expense.getFinanceAdmin() != null && expense.getFinanceAdmin().equals(user))) {
			return true;
		} else {
			return false;
		}
	}

	public boolean checkViewAuthorization(ExpenseItem expenseItem) {
		return checkViewAuthorization(expenseItem.getExpense());
	}

	public boolean checkViewAuthorizationMobile(ExpenseItem expenseItem, Token token) {
		return checkViewAuthorization(expenseItem.getExpense(), token.getUser());
	}

	public boolean checkViewAuthorizationMobile(Expense expense, Token token) {
		return checkViewAuthorization(expense, token.getUser());
	}

	public boolean checkViewAuthorization(Expense expense) {
		return checkViewAuthorization(expense, userService.getLoggedInUser());
	}

	private boolean checkViewAuthorization(Expense expense, User user) {
		if (user.getRoles().contains(UNI_ADMIN) && expense.getState().equals(PRINTED)) {
			return true;
		} else if (expense.getUser().equals(user)) {
			return true;
		} else if (expense.getAssignedManager() != null && expense.getAssignedManager().equals(user)) {
			return true;
		} else if (user.getRoles().contains(FINANCE_ADMIN)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean checkSignAuthorization(Expense expense) {
		return checkSignAuthorization(expense, userService.getLoggedInUser());
	}

	public boolean checkSignAuthorizationMobile(Expense expense, Token token) {
		return checkSignAuthorization(expense, token.getUser());
	}

	private boolean checkSignAuthorization(Expense expense, User user) {
		if (expense.getState().equals(TO_SIGN_BY_USER) && user.getRoles().contains(USER)) {
			return true;
		} else if (expense.getState().equals(TO_SIGN_BY_PROF) && user.getRoles().contains(PROF)) {
			return true;
		} else if (expense.getState().equals(TO_SIGN_BY_FINANCE_ADMIN) && user.getRoles().contains(FINANCE_ADMIN)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean checkDigitalSignatureDecision(Expense expense) {
		if (expense.getUser().equals(userService.getLoggedInUser()) && expense.getState().equals(TO_SIGN_BY_USER)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean checkAssignAuthorization(Expense expense) {
		if (expense.getExpenseItems().isEmpty()) {
			return false;
		} else {
			return true;
		}
	}
}