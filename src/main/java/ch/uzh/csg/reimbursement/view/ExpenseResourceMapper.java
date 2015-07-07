package ch.uzh.csg.reimbursement.view;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.csg.reimbursement.model.Expense;

@Service
public class ExpenseResourceMapper {

	@Autowired
	ExpenseMapper expenseMapper;

	public ExpenseResourceView map(String uid, Set<Expense> expenses) {

		ExpenseResourceView expenseResourceView = new ExpenseResourceView();
		expenseResourceView.setUid(uid);
		expenseResourceView.setMyExpenseItems(expenseMapper.map(expenses));
		expenseResourceView.setMyReviewExpenseItems(null);
		return expenseResourceView;
	}
}
