package ch.uzh.csg.reimbursement.repository;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ch.uzh.csg.reimbursement.model.CostCategory;
import ch.uzh.csg.reimbursement.model.Expense;
import ch.uzh.csg.reimbursement.model.ExpenseState;
import ch.uzh.csg.reimbursement.model.User;

public interface ExpenseRepository extends JpaRepository<Expense, Integer> {

	@Query("SELECT e FROM Expense e WHERE e.uid = :uid")
	public Expense findByUid(@Param("uid") String uid);

	@Query("SELECT e FROM Expense e JOIN e.user user WHERE user.uid = :uid AND NOT e.state = 'ARCHIVED'")
	public Set<Expense> findAllByUser(@Param("uid") String uid);

	@Query("SELECT e FROM Expense e JOIN e.assignedManager assignedManager WHERE assignedManager = :user AND NOT (e.state = 'ARCHIVED' OR e.state = 'PRINTED')")
	public Set<Expense> findAllByAssignedManager(@Param("user") User user);

	@Query("SELECT e FROM Expense e JOIN e.financeAdmin financeAdmin WHERE financeAdmin = :user AND NOT e.user = :user AND NOT (e.state = 'ARCHIVED' OR e.state = 'PRINTED')")
	public Set<Expense> findAllByFinanceAdmin(@Param("user") User user);

	@Query("SELECT e FROM Expense e WHERE e.state = :state AND NOT e.user = :user")
	public Set<Expense> findAllByStateWithoutUser(@Param("state") ExpenseState state, @Param("user") User user);

	@Query("SELECT e FROM Expense e WHERE e.state = :state AND e.user = :user")
	public Set<Expense> findAllByStateForUser(@Param("state") ExpenseState state, @Param("user") User user);

	@Query("SELECT e FROM Expense e Left OUTER JOIN e.expenseItems expenseItems WHERE (expenseItems.costCategory = :costCategory OR:costCategory is null) AND lower(e.accounting) LIKE lower(:accountingText) AND e.user IN :relevantUsers AND e.date >= :startTime AND e.date <= :endTime AND (e.state = :state OR:state is null)")
	public Set<Expense> search(@Param("relevantUsers") List<User> relevantUsers, @Param("accountingText") String accountingText, @Param("startTime") Date startTime, @Param("endTime") Date endTime, @Param("state") ExpenseState state, @Param("costCategory") CostCategory costCategory);

	@Query("SELECT COUNT(e) FROM Expense e WHERE e.state = :state")
	public int countByState(@Param("state") ExpenseState state);

	@Query("SELECT COUNT(e) FROM Expense e")
	public int countExpenses();

	@Query("SELECT SUM(e.totalAmount) FROM Expense e WHERE e.date BETWEEN :startDate AND :endDate")
	public Double sumTotalAmount(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

	@Query("SELECT e FROM Expense e WHERE e.state = 'PRINTED'")
	public List<Expense> getPrintedExpenses();

}
