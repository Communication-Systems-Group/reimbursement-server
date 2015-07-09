package ch.uzh.csg.reimbursement.service;

import java.util.Date;
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
import ch.uzh.csg.reimbursement.view.ExpenseResourceMapper;
import ch.uzh.csg.reimbursement.view.ExpenseResourceView;

@Service
@Transactional
public class ExpenseService {

	private final Logger LOG = LoggerFactory.getLogger(ExpenseService.class);

	@Autowired
	private ExpenseRepositoryProvider expenseRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private ExpenseResourceMapper expenseResourceMapper;

	public String create(ExpenseDto dto) {
		User user = userService.getLoggedInUser();
		//TODO Determine where contactPerson will be defined
		User contactPerson = userService.findByUid("cleib");
		Expense expense = new Expense(user, new Date(), contactPerson, dto.getBookingText(), dto.getState());
		expenseRepository.create(expense);

		return "{'expenseUid': '" + expense.getUid()+"'}";
	}

	public ExpenseResourceView findAllByUser(String uid) {
		return expenseResourceMapper.map(uid);
	}

	public ExpenseResourceView findAllByCurrentUser() {
		User user = userService.getLoggedInUser();
		return findAllByUser(user.getUid());
	}

	public void updateExpense(String uid, ExpenseDto dto) {
		Expense expense = findByUid(uid);
		//TODO Determine where contactPerson will be defined
		User contactPerson = userService.findByUid("cleib");
		User assignedManager = userService.findByUid(dto.getAssignedManagerUid());
		expense.updateExpense(new Date(), contactPerson, dto.getBookingText(), assignedManager, dto.getState());
	}

	public Expense findByUid(String uid) {
		Expense expense = expenseRepository.findByUid(uid);

		if (expense == null) {
			LOG.debug("Expense not found in database with uid: " + uid);
			throw new ExpenseNotFoundException();
		}
		return expense;
	}

	public Set<ExpenseItem> findAllExpenseItemsByUid(String uid) {
		Expense expense = findByUid(uid);
		return expense.getExpenseItems();
	}
}
