package ch.uzh.csg.reimbursement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ch.uzh.csg.reimbursement.model.EmailReceiver;

public interface EmailReceiverRepository extends JpaRepository<EmailReceiver, Integer> {

	@Query("SELECT e FROM EmailReceiver e WHERE e.uid = :uid")
	public EmailReceiver findByUid(@Param("uid") String uid);

	//TODO das chönnti öppert zum lafa bringa :)
	//	@Query("SELECT COUNT(e) FROM EmailReceiver e WHERE e.uid = :uid")
	//	public int contains(@Param("uid") String uid);
}
