package ch.uzh.csg.reimbursement.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.uzh.csg.reimbursement.dto.ExpenseDto;
import ch.uzh.csg.reimbursement.model.Expense;
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
		User contactPerson = userService.findByUid("null");

		Expense expense = new Expense(user, dto.getDate(), contactPerson, dto.getBookingText());
		expenseRepository.create(expense);
	}

	public List<Expense> findAll() {
		//TODO return only expenses of the user logged in
		return expenseRepository.findAll();
	}

	public void updateExpense(String uid, ExpenseDto dto) {
		Expense expense = findByUid(uid);
		User user = userService.findByUid(dto.getUserUid());
		//TODO Determine where contactPerson will be defined
		User contactPerson = userService.findByUid("null");

		expense.updateExpense(user, dto.getDate(), contactPerson, dto.getBookingText());
	}

	private Expense findByUid(String uid) {
		Expense expense = expenseRepository.findByUid(uid);
		return expense;
	}
}
