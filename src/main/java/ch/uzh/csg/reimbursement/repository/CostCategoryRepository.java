package ch.uzh.csg.reimbursement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ch.uzh.csg.reimbursement.model.CostCategory;

public interface CostCategoryRepository extends JpaRepository<CostCategory, Integer> {

	@Query("SELECT u FROM CostCategory u WHERE u.id = :id")
	public CostCategory findByUid(@Param("id") String id);
}

