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
		double exchangeRate = Double.parseDouble(dto.getExchangeRate());
		double amount = Double.parseDouble(dto.getAmount());
		ExpenseItem expenseItem = new ExpenseItem(dto.getDate(), expense, dto.getState(), dto.getCostCategoryUid(), dto.getReason(), dto.getCurrency(), exchangeRate, amount, dto.getProject());
		expenseItemRepository.create(expenseItem);
	}

}
