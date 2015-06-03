package ch.uzh.csg.reimbursement.service;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.uzh.csg.reimbursement.dto.ExpenseDto;
import ch.uzh.csg.reimbursement.model.Expense;
import ch.uzh.csg.reimbursement.model.ExpenseItem;
import ch.uzh.csg.reimbursement.model.User;
import ch.uzh.csg.reimbursement.repository.ExpenseRepositoryProvider;

@Service
@Transactional
public class ExpenseService {

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
		return expenseRepository.findAllByUser(uid);
	}

	public void updateExpense(String uid, ExpenseDto dto) {
		Expense expense = findByUid(uid);
		User user = userService.findByUid(dto.getUserUid());
		//TODO Determine where contactPerson will be defined
		User contactPerson = userService.findByUid(dto.getContactPersonUid());
		double totalAmount = computeTotalAmount(uid);
		expense.updateExpense(user, dto.getDate(), contactPerson, dto.getBookingText(), totalAmount);
	}

	public double computeTotalAmount(String uid) {
		Expense expense = findByUid(uid);
		double totalAmount=0;

		for(ExpenseItem item: expense.getExpenseItems()){
			totalAmount += totalAmount + item.getAmount();
		}
		return totalAmount;
	}

	public Expense findByUid(String uid) {
		return expenseRepository.findByUid(uid);
	}
}
