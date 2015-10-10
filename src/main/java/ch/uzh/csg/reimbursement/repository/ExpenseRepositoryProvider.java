package ch.uzh.csg.reimbursement.repository;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.csg.reimbursement.model.Expense;
import ch.uzh.csg.reimbursement.model.ExpenseState;
import ch.uzh.csg.reimbursement.model.User;

@Service
public class ExpenseRepositoryProvider {

	@Autowired
	private ExpenseRepository repository;

	public void create(Expense expense) {

		repository.save(expense);
	}

	public Set<Expense> findAllByUser(String uid) {

		return repository.findAllByUser(uid);
	}

	public Expense findByUid(String uid) {

		return repository.findByUid(uid);
	}

	public Set<Expense> findAllByAssignedManager(User user) {

		return repository.findAllByAssignedManager(user);
	}

	public Set<Expense> findAllByFinanceAdmin(User user) {

		return repository.findAllByFinanceAdmin(user);
	}

	public Set<Expense> findAllByState(ExpenseState state, User user) {

		return repository.findAllByState(state, user);
	}

	public void delete(Expense expense) {

		repository.delete(expense);
	}

	public Set<Expense> search(List<User> relevantUsers, String accountingText, Date fromDate, Date toDate) {
		return repository.search(relevantUsers, accountingText, fromDate, toDate);
	}

	public int countByState(ExpenseState state) {
		return repository.countByState(state);
	}

	public int countExpenses() {
		return repository.countExpenses();
	}
}