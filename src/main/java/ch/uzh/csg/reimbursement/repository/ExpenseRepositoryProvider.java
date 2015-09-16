package ch.uzh.csg.reimbursement.repository;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.csg.reimbursement.dto.SearchDto;
import ch.uzh.csg.reimbursement.model.Expense;
import ch.uzh.csg.reimbursement.model.ExpenseState;

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

	public Set<Expense> findAllByAssignedManager(String uid) {
		return repository.findAllByAssignedManager(uid);
	}

	public Set<Expense> findAllByState(ExpenseState state) {
		return repository.findAllByState(state);
	}

	public void delete(Expense expense) {
		repository.delete(expense);
	}

	public Set<Expense> findExpensesForAdminPool(SearchDto dto) {
		return repository.findExpensesForAdminPool(dto);
	}
}