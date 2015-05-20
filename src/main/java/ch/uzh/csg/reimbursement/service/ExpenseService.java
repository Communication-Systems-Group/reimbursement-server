package ch.uzh.csg.reimbursement.service;

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
		User contactPerson = userService.findByUid(dto.getContactPersonUid());

		Expense expense = new Expense(user, dto.getDate(), contactPerson, dto.getBookingText());
		expenseRepository.create(expense);
	}

	//TODO fix query in expense repository
	//	public List<Expense> findAllByUser(String uid) {
	//		User user = userService.findByUid(uid);
	//		int userId = user.getId();
	//		return expenseRepository.findAllByUser(userId);
	//	}

	public void updateExpense(String uid, ExpenseDto dto) {
		Expense expense = findByUid(uid);
		User user = userService.findByUid(dto.getUserUid());
		//TODO Determine where contactPerson will be defined
		User contactPerson = userService.findByUid(dto.getContactPersonUid());

		expense.updateExpense(user, dto.getDate(), contactPerson, dto.getBookingText());
	}

	private Expense findByUid(String uid) {
		Expense expense = expenseRepository.findByUid(uid);
		return expense;
	}
}
