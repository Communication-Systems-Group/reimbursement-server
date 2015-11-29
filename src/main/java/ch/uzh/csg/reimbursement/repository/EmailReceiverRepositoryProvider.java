package ch.uzh.csg.reimbursement.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.csg.reimbursement.model.EmailReceiver;

@Service
public class EmailReceiverRepositoryProvider {

	@Autowired
	private EmailReceiverRepository emailReceiverRepository;

	public void create(EmailReceiver receiver) {

		emailReceiverRepository.save(receiver);
	}

	public EmailReceiver findByUid(String uid) {

		return emailReceiverRepository.findByUid(uid);
	}

	public void delete(EmailReceiver receiver) {

		emailReceiverRepository.delete(receiver);
	}

	public void deleteAll(){
		emailReceiverRepository.deleteAll();
	}

	public List<EmailReceiver> findAll(){
		return emailReceiverRepository.findAll();
	}

	public boolean contains(String receiver_uid){
		return emailReceiverRepository.contains(receiver_uid)>0;
	}
}
