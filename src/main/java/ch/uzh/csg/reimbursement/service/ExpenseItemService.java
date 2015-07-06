package ch.uzh.csg.reimbursement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.uzh.csg.reimbursement.dto.ExpenseItemDto;
import ch.uzh.csg.reimbursement.model.CostCategory;
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

	@Autowired
	private CostCategoryService costCategoryService;

	public void create(ExpenseItemDto dto) {
		Expense expense = expenseService.findByUid(dto.getExpenseUid());
		CostCategory category = costCategoryService.findByUid(dto.getCostCategoryUid());
		ExpenseItem expenseItem = new ExpenseItem(dto.getDate(), category, dto.getReason(), dto.getCurrency(), dto.getExchangeRate(), dto.getAmount(), dto.getProject(), expense);
		expenseItemRepository.create(expenseItem);

	}

	public void updateExpenseItem(String uid, ExpenseItemDto dto) {
		ExpenseItem expenseItem = expenseItemRepository.findByUid(uid);
		CostCategory category = costCategoryService.findByUid(dto.getCostCategoryUid());
		expenseItem.updateExpenseItem(dto.getDate(), category, dto.getReason(), dto.getCurrency(), dto.getExchangeRate(), dto.getAmount(), dto.getProject());
		expenseItemRepository.create(expenseItem);

	}

	public ExpenseItem findByUid(String uid) {
		return expenseItemRepository.findByUid(uid);
	}
}
