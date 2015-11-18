package ch.uzh.csg.reimbursement.repository;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.csg.reimbursement.model.EmailReceiver;

@Service
public class EmailReceiverRepositoryProvider {

	@Autowired
	private EmailReceiverRepository emailReceiverRepository;

	private final Logger LOG = LoggerFactory.getLogger(EmailReceiverRepository.class);

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

	//TODO die methoda mit dr exception isch nidso schön, hann di ander gschriba aber wais nid öb die funzt- hann no anderi fehler ir db kah
	//TODO in dr DB müesstima auno t'restriction ihfüega dass in dr liista nur immr 1 gliichi uid isch, so wie halt überall
	public boolean contains(String receiver_uid){
		try {
			EmailReceiver er = emailReceiverRepository.findByUid(receiver_uid);
			LOG.error("The uid is already in the table: "+er.getUid()+ " return true");
			return true;
		} catch (Exception e) {
			LOG.error("The uid is not in the table: return false");
			return false;
		}
	}

	//TODO das chönnti öppert zum lafa bringa :)
	//	public boolean contains(String receiver_uid){
	//		return emailReceiverRepository.contains(receiver_uid)>0;
	//	}
}
