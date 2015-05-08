package ch.uzh.csg.reimbursement.service;

import java.util.List;

import javax.persistence.Transient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import ch.uzh.csg.reimbursement.dto.CroppingDto;
import ch.uzh.csg.reimbursement.dto.UserDto;
import ch.uzh.csg.reimbursement.model.Signature;
import ch.uzh.csg.reimbursement.model.User;
import ch.uzh.csg.reimbursement.model.exception.SignatureNotFoundException;
import ch.uzh.csg.reimbursement.model.exception.UserNotFoundException;
import ch.uzh.csg.reimbursement.repository.UserRepositoryProvider;

@Service
@Transactional
public class UserService {

	@Transient
	private final Logger Logger = LoggerFactory.getLogger(Signature.class);

	@Autowired
	private UserRepositoryProvider repository;

	public void create(UserDto dto) {
		User user = new User(dto.getFirstName(), dto.getLastName());
		repository.create(user);
	}

	public List<User> findAll() {
		return repository.findAll();
	}

	public User findByUid(String uid) {
		User user = repository.findByUid(uid);

		if(user == null) {
			Logger.debug("User return value is Null");
			throw new UserNotFoundException();
		}
		else {
			return user;
		}
	}

	public void removeByUid(String uid) {
		repository.delete(findByUid(uid));
	}

	public void updateFirstName(String uid, String firstName) {
		User user = findByUid(uid);
		user.setFirstName(firstName);
	}

	public void addSignature(String uid, MultipartFile file) {
		User user = findByUid(uid);
		user.setSignature(file);
	}

	public byte[] getSignature(String uid) {
		User user = findByUid(uid);

		if(user.getSignature() == null) {
			Logger.debug("No signature found for user:" + user);
			throw new SignatureNotFoundException();
		}
		else {
			return user.getSignature();
		}
	}

	public void addSignatureCropping(String uid, CroppingDto dto) {
		User user = findByUid(uid);
		user.addSignatureCropping(dto.getWidth(), dto.getHeight(), dto.getTop(), dto.getLeft());
	}

}
