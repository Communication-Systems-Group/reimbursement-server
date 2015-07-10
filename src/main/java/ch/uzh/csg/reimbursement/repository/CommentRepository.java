package ch.uzh.csg.reimbursement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ch.uzh.csg.reimbursement.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

	@Query("SELECT c FROM Comment c WHERE c.uid = :uid")
	public Comment findByUid(@Param("uid") String uid);
}
