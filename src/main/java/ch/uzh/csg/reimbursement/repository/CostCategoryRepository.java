package ch.uzh.csg.reimbursement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ch.uzh.csg.reimbursement.model.CostCategory;

public interface CostCategoryRepository extends JpaRepository<CostCategory, Integer> {

	@Query("SELECT c FROM CostCategory c WHERE c.uid = :uid")
	public CostCategory findByUid(@Param("uid") String uid);
}
