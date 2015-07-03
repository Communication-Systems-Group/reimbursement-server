package ch.uzh.csg.reimbursement.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.uzh.csg.reimbursement.dto.CostCategoryDto;
import ch.uzh.csg.reimbursement.model.CostCategory;
import ch.uzh.csg.reimbursement.repository.CostCategoryRepositoryProvider;

@Service
@Transactional
public class CostCategoryService {

	@Autowired
	private CostCategoryRepositoryProvider costCategoryRepository;

	public List<CostCategory> findAll() {
		return costCategoryRepository.findAll();
	}

	public void create(CostCategoryDto dto) {
		CostCategory costCategory = new CostCategory(dto.getName(), dto.getDescription(), dto.getAccountingPolicy());
		costCategoryRepository.create(costCategory);
	}
}
