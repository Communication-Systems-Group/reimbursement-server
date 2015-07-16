package ch.uzh.csg.reimbursement.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.csg.reimbursement.model.Account;

@Service
public class AccountRepositoryProvider {

	@Autowired
	private AccountRepository accountRepository;

	public List<Account> findAll() {
		return accountRepository.findAll();
	}

	public void create(Account account) {
		accountRepository.save(account);
	}

	public Account findByUid(String uid) {
		return accountRepository.findByUid(uid);
	}

	public void delete(Account account) {
		accountRepository.delete(account);
	}
}
