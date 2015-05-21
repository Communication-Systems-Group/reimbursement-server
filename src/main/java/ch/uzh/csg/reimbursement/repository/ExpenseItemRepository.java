package ch.uzh.csg.reimbursement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ch.uzh.csg.reimbursement.model.ExpenseItem;

public interface ExpenseItemRepository extends JpaRepository<ExpenseItem, Integer> {

	//	@Query("SELECT u FROM User u WHERE u.uid = :uid")
	//	public User findByUid(@Param("uid") String uid);

}
