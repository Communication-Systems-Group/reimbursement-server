package ch.uzh.csg.reimbursement.repository;

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

	//	public List<Expense> findAllByUser(int userId) {
	//		return repository.findAllByUser(userId);
	//	}

	public Expense findByUid(String uid) {
		return repository.findByUid(uid);
	}

}