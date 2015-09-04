package ch.uzh.csg.reimbursement.view;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.csg.reimbursement.model.Expense;

@Service
public class ExpenseMapper {

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private NoteMapper noteMapper;

	@Autowired
	private ExpenseItemMapper expenseItemMapper;

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
		if (expense.getAssignedManager() != null) {
			mappedExpense.setAssignedManagerUid(expense.getAssignedManager().getUid());
		}
		mappedExpense.setAmount(expense.getTotalAmount());
		mappedExpense.setAccounting(expense.getAccounting());
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
		mappedReviewExpense.setAccount(expense.getAccounting());

		return mappedReviewExpense;
	}

	public ExpenseDetailedView mapExpenseDetailedView(Expense expense) {
		ExpenseDetailedView expenseDetailedView = new ExpenseDetailedView();
		expenseDetailedView.setUid(expense.getUid());
		expenseDetailedView.setCreator(userMapper.mapUser(expense.getUser()));
		if (expense.getFinanceAdmin() != null) {
			expenseDetailedView.setFinanceAdmin(userMapper.mapUser(expense.getFinanceAdmin()));
		}
		expenseDetailedView.setAccounting(expense.getAccounting());
		expenseDetailedView.setNote(noteMapper.mapNote(expense.getComments()));
		expenseDetailedView.setExpenseItems(expenseItemMapper.mapExpenseItem(expense.getExpenseItems()));
		return expenseDetailedView;
	}
}
