package ch.uzh.csg.reimbursement.repository;

import java.util.List;

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

	public void update(Expense expense) {
		repository.save(expense);
	}

	public void delete(Expense expense) {
		repository.delete(expense);
	}

	public List<Expense> findAll() {
		return repository.findAll();
	}

	public Expense findByUid(String uid) {
		return repository.findByUid(uid);
	}

}

