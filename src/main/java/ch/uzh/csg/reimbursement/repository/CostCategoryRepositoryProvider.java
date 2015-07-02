package ch.uzh.csg.reimbursement.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.csg.reimbursement.model.CostCategory;

@Service
public class CostCategoryRepositoryProvider {

	@Autowired
	private CostCategoryRepository costCategoryRepository;

	public List<CostCategory> findAll() {
		return costCategoryRepository.findAll();
	}

}
