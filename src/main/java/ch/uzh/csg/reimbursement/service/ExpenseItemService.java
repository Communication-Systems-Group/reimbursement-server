package ch.uzh.csg.reimbursement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.uzh.csg.reimbursement.dto.ExpenseItemDto;
import ch.uzh.csg.reimbursement.model.Expense;
import ch.uzh.csg.reimbursement.model.ExpenseItem;
import ch.uzh.csg.reimbursement.repository.ExpenseItemRepositoryProvider;

@Service
@Transactional
public class ExpenseItemService {

	@Autowired
	ExpenseItemRepositoryProvider expenseItemRepository;
	@Autowired
	private ExpenseService expenseService;

	public void create(ExpenseItemDto dto) {
		Expense expense = expenseService.findByUid(dto.getExpenseUid());
		ExpenseItem expenseItem = new ExpenseItem(dto.getDate(), expense, dto.getState(), dto.getCostCategoryUid(), dto.getReason(), dto.getCurrency(), dto.getExchangeRate(), dto.getAmount(), dto.getProject());
		expenseItemRepository.create(expenseItem);
	}
}
