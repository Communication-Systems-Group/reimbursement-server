package ch.uzh.csg.reimbursement.view;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;

import ch.uzh.csg.reimbursement.model.Expense;

@Service
public class ExpenseMapper {

	public Set<ExpenseView> map(Set<Expense> expenses) {
		Set<ExpenseView> mappedExpenses = new HashSet<ExpenseView>();
		for(Expense expense: expenses) {
			ExpenseView tmp = new ExpenseView();
			tmp.setUid(expense.getUid());
			tmp.setDate(expense.getDate());
			tmp.setState(expense.getState());
			tmp.setAmount(expense.getTotalAmount());
			tmp.setAccount(expense.getBookingText());
			mappedExpenses.add(tmp);
		}
		return mappedExpenses;
	}
}
