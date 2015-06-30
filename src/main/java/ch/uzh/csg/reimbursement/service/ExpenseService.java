package ch.uzh.csg.reimbursement.service;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.uzh.csg.reimbursement.dto.ExpenseDto;
import ch.uzh.csg.reimbursement.model.Expense;
import ch.uzh.csg.reimbursement.model.ExpenseItem;
import ch.uzh.csg.reimbursement.model.User;
import ch.uzh.csg.reimbursement.model.exception.ExpenseNotFoundException;
import ch.uzh.csg.reimbursement.repository.ExpenseRepositoryProvider;

@Service
@Transactional
public class ExpenseService {

	private final Logger LOG = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private ExpenseRepositoryProvider expenseRepository;

	@Autowired
	private UserService userService;

	public void create(ExpenseDto dto) {
		User user = userService.findByUid(dto.getUserUid());
		//TODO Determine where contactPerson will be defined
		User contactPerson = userService.findByUid(dto.getContactPersonUid());

		Expense expense = new Expense(user, dto.getDate(), contactPerson, dto.getBookingText());
		expenseRepository.create(expense);
	}

	public Set<Expense> findAllByUser(String uid) {
		Set<Expense> expenses = expenseRepository.findAllByUser(uid);
		if (expenses.isEmpty()) {
			LOG.debug("No expenses found for the user wih the uid: " + uid);
			throw new ExpenseNotFoundException();
		}
		return expenses;
	}

	public Set<Expense> findAllByCurrentUser() {
		User user = userService.getLoggedInUser();
		Set<Expense> expenses = expenseRepository.findAllByUser(user.getUid());
		if (expenses.isEmpty()) {
			LOG.debug("No expenses found for the user wih the uid: " + user.getUid());
			throw new ExpenseNotFoundException();
		}
		return expenses;
	}

	public void updateExpense(String uid, ExpenseDto dto) {
		Expense expense = findByUid(uid);
		//TODO Determine where contactPerson will be defined
		User contactPerson = userService.findByUid(dto.getContactPersonUid());
		expense.updateExpense(dto.getDate(), contactPerson, dto.getBookingText());
	}

	public void computeTotalAmount(String uid) {
		Expense expense = findByUid(uid);
		double totalAmount=0;

		for(ExpenseItem item: expense.getExpenseItems()){
			totalAmount += totalAmount + item.getAmount();
		}
		expense.setTotalAmount(totalAmount);
	}

	//TODO @Dave addExpenseItem seems not to work
	public void addExpenseItem(String uid, ExpenseItem expenseItem){
		Expense expense = findByUid(uid);
		expense.getExpenseItems().add(expenseItem);
	}

	public Expense findByUid(String uid) {
		return expenseRepository.findByUid(uid);
	}

	public Set<ExpenseItem> findAllExpenseItemsByUid(String uid) {
		Expense expense = findByUid(uid);
		return expense.getExpenseItems();
	}
}
