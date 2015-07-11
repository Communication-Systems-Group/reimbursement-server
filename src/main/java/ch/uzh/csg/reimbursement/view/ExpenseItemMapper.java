package ch.uzh.csg.reimbursement.view;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;

import ch.uzh.csg.reimbursement.model.ExpenseItem;

@Service
public class ExpenseItemMapper {

	public ExpenseItemView mapExpenseItem(ExpenseItem expenseItem) {
		ExpenseItemView mappedExpense = new ExpenseItemView();
		mappedExpense.setUid(expenseItem.getUid());
		mappedExpense.setDate(expenseItem.getDate());
		mappedExpense.setCostCategory(expenseItem.getCostCategory().getName());
		mappedExpense.setDescription(expenseItem.getReason());
		mappedExpense.setAmount(new AmountView(expenseItem.getAmount(), expenseItem.getExchangeRate(), expenseItem.getCurrency()));
		mappedExpense.setCost_center(expenseItem.getProject());
		mappedExpense.setAttachment(expenseItem.getExpenseItemAttachment());
		return mappedExpense;
	}

	public Set<ExpenseItemView> mapExpenseItem(Set<ExpenseItem> expenseItems) {
		Set<ExpenseItemView> mappedExpenses = new HashSet<ExpenseItemView>();

		for(ExpenseItem expenseItem: expenseItems) {
			mappedExpenses.add(mapExpenseItem(expenseItem));
		}
		return mappedExpenses;
	}
}
