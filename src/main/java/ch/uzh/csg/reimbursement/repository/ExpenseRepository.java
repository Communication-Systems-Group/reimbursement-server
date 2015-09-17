package ch.uzh.csg.reimbursement.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ch.uzh.csg.reimbursement.model.Expense;
import ch.uzh.csg.reimbursement.model.ExpenseState;

public interface ExpenseRepository extends JpaRepository<Expense, Integer> {

	@Query("SELECT e FROM Expense e WHERE e.uid = :uid")
	public Expense findByUid(@Param("uid") String uid);

	@Query("SELECT e FROM Expense e JOIN e.user user WHERE user.uid = :uid")
	public Set<Expense> findAllByUser(@Param("uid") String uid);

	@Query("SELECT e FROM Expense e JOIN e.assignedManager assignedManager WHERE assignedManager.uid = :uid")
	public Set<Expense> findAllByAssignedManager(@Param("uid") String uid);

	@Query("SELECT e FROM Expense e WHERE e.state = :state")
	public Set<Expense> findAllByState(@Param("state") ExpenseState state);

	//TODO implement hql
	//	@Query("SELECT e FROM Expense e JOIN e.user user WHERE user.lastName = :dto")
	//	public Set<Expense> findExpensesForAdminPool(@Param("dto") String dto);
}
