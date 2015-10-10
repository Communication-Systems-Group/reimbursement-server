package ch.uzh.csg.reimbursement.repository;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ch.uzh.csg.reimbursement.model.Expense;
import ch.uzh.csg.reimbursement.model.ExpenseState;
import ch.uzh.csg.reimbursement.model.User;

public interface ExpenseRepository extends JpaRepository<Expense, Integer> {

	@Query("SELECT e FROM Expense e WHERE e.uid = :uid")
	public Expense findByUid(@Param("uid") String uid);

	@Query("SELECT e FROM Expense e JOIN e.user user WHERE user.uid = :uid")
	public Set<Expense> findAllByUser(@Param("uid") String uid);

	@Query("SELECT e FROM Expense e JOIN e.assignedManager assignedManager WHERE assignedManager = :user")
	public Set<Expense> findAllByAssignedManager(@Param("user") User user);

	@Query("SELECT e FROM Expense e JOIN e.financeAdmin financeAdmin WHERE financeAdmin = :user AND NOT e.user = :user")
	public Set<Expense> findAllByFinanceAdmin(@Param("user") User user);

	@Query("SELECT e FROM Expense e WHERE e.state = :state AND NOT e.user = :user")
	public Set<Expense> findAllByState(@Param("state") ExpenseState state, @Param("user") User user);

	@Query("SELECT e FROM Expense e WHERE lower(e.accounting) LIKE lower(:accountingText) AND e.user IN :relevantUsers AND e.date >= :fromDate AND e.date <= :toDate")
	public Set<Expense> search(@Param("relevantUsers") List<User> relevantUsers, @Param("accountingText") String accountingText, @Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

	@Query("SELECT COUNT(e) FROM Expense e WHERE e.state = :state")
	public int countByState(@Param("state") ExpenseState state);

	@Query("SELECT COUNT(e) FROM Expense e")
	public int countExpenses();

}
