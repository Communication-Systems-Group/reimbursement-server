package ch.uzh.csg.reimbursement.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.csg.reimbursement.model.ExpenseItem;

@Service
public class ExpenseItemRepositoryProvider {

	@Autowired
	private ExpenseItemRepository expenseItemRepository;

	public void create(ExpenseItem expenseItem) {
		expenseItemRepository.save(expenseItem);
	}
}
