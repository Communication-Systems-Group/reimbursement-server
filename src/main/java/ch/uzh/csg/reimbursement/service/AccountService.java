package ch.uzh.csg.reimbursement.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.uzh.csg.reimbursement.model.Account;
import ch.uzh.csg.reimbursement.repository.AccountRepositoryProvider;

@Service
@Transactional
public class AccountService {

	@Autowired
	private AccountRepositoryProvider repository;

	public List<Account> findAll() {
		return repository.findAll();
	}
}
