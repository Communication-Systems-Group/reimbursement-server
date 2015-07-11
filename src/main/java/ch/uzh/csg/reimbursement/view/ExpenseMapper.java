package ch.uzh.csg.reimbursement.view;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.csg.reimbursement.model.Expense;

@Service
public class ExpenseMapper {

	@Autowired
	UserMapper userMapper;

	@Autowired
	NoteMapper noteMapper;

	@Autowired
	ExpenseItemMapper expenseItemMapper;

	public Set<ExpenseView> mapExpense(Set<Expense> expenses) {
		Set<ExpenseView> mappedExpenses = new HashSet<ExpenseView>();
		for(Expense expense: expenses) {
			mappedExpenses.add(mapExpense(expense));
		}
		return mappedExpenses;
	}

	public ExpenseView mapExpense(Expense expense) {
		ExpenseView mappedExpense = new ExpenseView();
		mappedExpense.setUid(expense.getUid());
		mappedExpense.setDate(expense.getDate());
		mappedExpense.setState(expense.getState());
		mappedExpense.setAmount(expense.getTotalAmount());
		mappedExpense.setAccount(expense.getBookingText());
		return mappedExpense;
	}

	public Set<ReviewExpenseView> mapReviewExpense(Set<Expense> expenses) {
		Set<ReviewExpenseView> mappedReviewExpenses = new HashSet<ReviewExpenseView>();

		for(Expense expense: expenses) {
			mappedReviewExpenses.add(mapReviewExpense(expense));
		}
		return mappedReviewExpenses;
	}

	public ReviewExpenseView mapReviewExpense(Expense expense) {
		ReviewExpenseView mappedReviewExpense = new ReviewExpenseView();
		mappedReviewExpense.setUid(expense.getUid());
		mappedReviewExpense.setCreator(userMapper.mapUser(expense.getUser()));
		mappedReviewExpense.setDate(expense.getDate());
		mappedReviewExpense.setAmount(expense.getTotalAmount());
		mappedReviewExpense.setAccount(expense.getBookingText());

		return mappedReviewExpense;
	}

	public ExpenseDetailedView mapExpenseDetailedView(Expense expense) {
		ExpenseDetailedView expenseDetailedView = new ExpenseDetailedView();
		expenseDetailedView.setUid(expense.getUid());
		expenseDetailedView.setCreator(userMapper.mapUser(expense.getUser()));
		expenseDetailedView.setContact(userMapper.mapUser(expense.getContactPerson()));
		expenseDetailedView.setAccounting(expense.getBookingText());
		expenseDetailedView.setNote(noteMapper.mapNote(expense.getComments()));
		expenseDetailedView.setReceipts(expenseItemMapper.mapExpenseItem(expense.getExpenseItems()));
		return expenseDetailedView;
	}
}
