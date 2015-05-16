package ch.uzh.csg.reimbursement.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.uzh.csg.reimbursement.dto.ExpenseDto;
import ch.uzh.csg.reimbursement.model.Expense;
import ch.uzh.csg.reimbursement.model.User;
import ch.uzh.csg.reimbursement.repository.ExpenseRepositoryProvider;
import ch.uzh.csg.reimbursement.repository.UserRepositoryProvider;

@Service
@Transactional
public class ExpenseService {

	@Autowired
	private ExpenseRepositoryProvider expenseRepository;

	@Autowired
	private UserRepositoryProvider userRepository;

	public void create(ExpenseDto dto) {
		User user = userRepository.findByUid(dto.getUserId());
		//TODO Determine where contactPerson will be defined
		User contactPerson = userRepository.findByUid("null");

		Expense expense = new Expense(user, dto.getDate(), contactPerson, dto.getBookingText());
		expenseRepository.create(expense);
	}

	public List<Expense> findAll() {
		// TODO Auto-generated method stub

		return null;
	}

	public void updateExpense(String uid, Expense dto) {
		Expense expense = expenseRepository.findByUid(uid);
		expense.setDate(dto.getDate());
		expense.setContactPerson(dto.getContactPerson());
		expense.setBookingText(dto.getBookingText());
		expense.setUser(dto.getUser());

		expenseRepository.update(expense);
	}

	public void removeByUid(String uid) {
		expenseRepository.delete(findByUid(uid));
	}

	private Expense findByUid(String uid) {
		Expense expense = expenseRepository.findByUid(uid);
		return expense;
	}
}
