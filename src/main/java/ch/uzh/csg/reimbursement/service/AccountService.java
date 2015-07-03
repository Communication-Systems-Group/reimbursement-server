package ch.uzh.csg.reimbursement.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.uzh.csg.reimbursement.dto.AccountDto;
import ch.uzh.csg.reimbursement.model.Account;
import ch.uzh.csg.reimbursement.model.CostCategory;
import ch.uzh.csg.reimbursement.repository.AccountRepositoryProvider;
import ch.uzh.csg.reimbursement.repository.CostCategoryRepositoryProvider;

@Service
@Transactional
public class AccountService {

	@Autowired
	private AccountRepositoryProvider accountRepository;

	@Autowired
	private CostCategoryRepositoryProvider costCategoryRepository;

	public List<Account> findAll() {
		return accountRepository.findAll();
	}

	public void create(AccountDto dto) {
		CostCategory category = costCategoryRepository.findByUid(dto.getCostCategoryUid());
		Account account = new Account(category, dto.getNumber());
		accountRepository.create(account);
	}
}
