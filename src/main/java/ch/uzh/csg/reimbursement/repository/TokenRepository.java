package ch.uzh.csg.reimbursement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ch.uzh.csg.reimbursement.model.Token;
import ch.uzh.csg.reimbursement.model.TokenType;
import ch.uzh.csg.reimbursement.model.User;

public interface TokenRepository extends JpaRepository<Token, Integer>{

	@Query("SELECT t FROM Token t WHERE t.uid = :uid")
	public Token findByUid(@Param("uid") String uid);

	@Query("SELECT t FROM Token t WHERE type = :type AND user = :user")
	public Token findByTypeAndUser(@Param("type") TokenType type, @Param("user") User user);

}
