package ch.uzh.csg.reimbursement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ch.uzh.csg.reimbursement.model.ExpenseItem;

public interface ExpenseItemRepository extends JpaRepository<ExpenseItem, Integer> {

	@Query("SELECT e FROM ExpenseItem e WHERE e.uid = :uid")
	public ExpenseItem findByUid(@Param("uid") String uid);

}
