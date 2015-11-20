package ch.uzh.csg.reimbursement.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.uzh.csg.reimbursement.application.validation.ValidationService;
import ch.uzh.csg.reimbursement.dto.CostCategoryDto;
import ch.uzh.csg.reimbursement.model.CostCategory;
import ch.uzh.csg.reimbursement.model.exception.CostCategoryNotFoundException;
import ch.uzh.csg.reimbursement.model.exception.LastActiveCostCategoryCannotBeDeactivatedException;
import ch.uzh.csg.reimbursement.model.exception.ValidationException;
import ch.uzh.csg.reimbursement.repository.CostCategoryRepositoryProvider;

@Service
@Transactional
public class CostCategoryService {

	private final Logger LOG = LoggerFactory.getLogger(CostCategoryService.class);

	@Autowired
	private CostCategoryRepositoryProvider costCategoryRepository;

	@Autowired
	private ValidationService validationService;

	public List<CostCategory> getAll() {
		return costCategoryRepository.findAll();

	}

	public List<CostCategory> getAllActive() {
		return costCategoryRepository.findAllActive();
	}

	public CostCategory createCostCategory(CostCategoryDto dto) {
		String keyDescription = "admin.costCategories.description";
		String keyName = "admin.costCategories.name";
		String keyNumber = "admin.costCategories.number";

		if (validationService.matches(keyDescription, dto.getDescription().getDe())
				&& validationService.matches(keyDescription, dto.getDescription().getEn())
				&& validationService.matches(keyName, dto.getName().getDe())
				&& validationService.matches(keyName, dto.getName().getEn())
				&& validationService.matches(keyNumber, Integer.toString(dto.getAccountNumber()))) {
			CostCategory costCategory = new CostCategory(dto);
			costCategoryRepository.create(costCategory);
			return costCategory;
		} else {
			throw new ValidationException(keyDescription + " | " + keyName + " | " + keyNumber);
		}
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
		List<CostCategory> activeCostCategories = getAllActive();

		if(activeCostCategories.size() > 1) {
			CostCategory costCategory = getByUid(uid);
			costCategory.setIsActive(false);
		}
		else {
			throw new LastActiveCostCategoryCannotBeDeactivatedException();
		}
	}

	public void activateCostCategory(String uid) {
		CostCategory costCategory = getByUid(uid);
		costCategory.setIsActive(true);
	}
}
