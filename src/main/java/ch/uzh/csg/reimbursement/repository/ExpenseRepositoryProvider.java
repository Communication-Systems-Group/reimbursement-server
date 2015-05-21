package ch.uzh.csg.reimbursement.repository;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.csg.reimbursement.model.Expense;

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

}