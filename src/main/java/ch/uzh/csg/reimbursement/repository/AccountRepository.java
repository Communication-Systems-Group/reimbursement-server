package ch.uzh.csg.reimbursement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ch.uzh.csg.reimbursement.model.Account;

public interface AccountRepository extends JpaRepository<Account, Integer> {

	@Query("SELECT u FROM Account u WHERE u.id = :id")
	public Account findById(@Param("id") String id);
}
