package ch.uzh.csg.reimbursement.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.uzh.csg.reimbursement.dto.CostCategoryDto;
import ch.uzh.csg.reimbursement.model.CostCategory;
import ch.uzh.csg.reimbursement.model.exception.CostCategoryNotFoundException;
import ch.uzh.csg.reimbursement.repository.CostCategoryRepositoryProvider;

@Service
@Transactional
public class CostCategoryService {

	private final Logger LOG = LoggerFactory.getLogger(CostCategoryService.class);

	@Autowired
	private CostCategoryRepositoryProvider costCategoryRepository;

	public List<CostCategory> getAll() {
		return costCategoryRepository.findAll();
	}

	public CostCategory createCostCategory(CostCategoryDto dto) {
		CostCategory costCategory = new CostCategory(dto);
		costCategoryRepository.create(costCategory);
		return costCategory;
	}

	public CostCategory getByUid(String uid) {
		CostCategory costCategory = costCategoryRepository.findByUid(uid);

		if (costCategory == null) {
			LOG.debug("CostCategory not found in database with uid: " + uid);
			throw new CostCategoryNotFoundException();
		}
		return costCategory;
	}

	public void updateCostCategory(String uid, CostCategoryDto dto) {
		CostCategory costCategory = getByUid(uid);
		costCategory.updateCostCategory(dto);
	}

	public void deactivateCostCategory(String uid) {
		CostCategory costCategory = getByUid(uid);
		costCategory.deactivate();
	}

	public void activateCostCategory(String uid) {
		CostCategory costCategory = getByUid(uid);
		costCategory.activate();
	}
}
