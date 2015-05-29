package ch.uzh.csg.reimbursement.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import ch.uzh.csg.reimbursement.dto.CroppingDto;
import ch.uzh.csg.reimbursement.ldap.LdapPerson;
import ch.uzh.csg.reimbursement.model.Signature;
import ch.uzh.csg.reimbursement.model.User;
import ch.uzh.csg.reimbursement.model.exception.SignatureNotFoundException;
import ch.uzh.csg.reimbursement.model.exception.UserNotFoundException;
import ch.uzh.csg.reimbursement.repository.UserRepositoryProvider;

@Service
@Transactional
public class UserService {

	private final Logger Logger = LoggerFactory.getLogger(Signature.class);

	@Autowired
	private UserRepositoryProvider repository;

	public List<User> findAll() {
		return repository.findAll();
	}

	public User findByUid(String uid) {
		User user = repository.findByUid(uid);

		if(user == null) {
			Logger.debug("User not found in database with uid: " + uid);
			throw new UserNotFoundException();
		}
		return user;
	}

	public void addSignature(String uid, MultipartFile file) {
		User user = findByUid(uid);
		user.setSignature(file);
	}

	public byte[] getSignature(String uid) {
		User user = findByUid(uid);

		if (user.getSignature() == null) {
			Logger.debug("No signature found for user with uid: " + uid);
			throw new SignatureNotFoundException();
		}
		return user.getSignature();
	}

	public void addSignatureCropping(String uid, CroppingDto dto) {
		User user = findByUid(uid);
		user.addSignatureCropping(dto.getWidth(), dto.getHeight(), dto.getTop(), dto.getLeft());
	}

	public void synchronize(List<LdapPerson> ldapPersons) {
		for (LdapPerson ldapPerson : ldapPersons) {
			User user = repository.findByUid(ldapPerson.getUid());

			if (user != null) {
				user.setFirstName(ldapPerson.getFirstName());
				user.setLastName(ldapPerson.getLastName());
				user.setEmail(ldapPerson.getEmail());
				user.setManagerName(ldapPerson.getManager());

			} else {
				user = new User(ldapPerson.getFirstName(), ldapPerson.getLastName(), ldapPerson.getUid(),
						ldapPerson.getEmail(), ldapPerson.getManager());

				repository.create(user);
			}
		}

		// Find the uid of the manager and save it
		List<User> users1 = repository.findAll();
		for(User user1 : users1) {
			List<User> users2 = repository.findAll();
			for(User user2 : users2) {
				if(user1.getManagerName() != null && user1.getManagerName().equals(user2.getUid())) {
					user1.setManager(user2);
				}
			}
		}
	}

}
