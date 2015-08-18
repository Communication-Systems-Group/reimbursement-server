package ch.uzh.csg.reimbursement.view;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.csg.reimbursement.model.Expense;
import ch.uzh.csg.reimbursement.model.ExpenseState;
import ch.uzh.csg.reimbursement.model.Role;
import ch.uzh.csg.reimbursement.repository.ExpenseRepository;
import ch.uzh.csg.reimbursement.service.UserService;

@Service
public class ExpenseResourceMapper {

	@Autowired
	private ExpenseMapper expenseMapper;

	@Autowired
	private ExpenseRepository expenseRepository;

	@Autowired
	private UserService userService;

	public ExpenseResourceView map(String uid) {

		Set<Expense> expenses = expenseRepository.findAllByUser(uid);
		Set<Expense> reviewExpenses = expenseRepository.findAllByAssignedManager(uid);
		if(userService.getLoggedInUser().getRoles().contains(Role.CONTACTPERSON)) {
			for(Expense expense: expenseRepository.findAllByState(ExpenseState.ASSIGNED_TO_CONTACTPERSON)) {
				reviewExpenses.add(expense);
			}
		}
		ExpenseResourceView expenseResourceView = new ExpenseResourceView();
		expenseResourceView.setUid(uid);
		expenseResourceView.setMyExpenses(expenseMapper.mapExpense(expenses));
		expenseResourceView.setMyReviewExpenses(expenseMapper.mapReviewExpense(reviewExpenses));
		return expenseResourceView;
	}
}
