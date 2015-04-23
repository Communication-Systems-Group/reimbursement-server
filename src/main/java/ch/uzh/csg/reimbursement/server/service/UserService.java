package ch.uzh.csg.reimbursement.server.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import ch.uzh.csg.reimbursement.server.dto.UserDto;
import ch.uzh.csg.reimbursement.server.model.User;
import ch.uzh.csg.reimbursement.server.repository.UserRepositoryProvider;

@Service
@Transactional
public class UserService {

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
		return repository.findByUid(uid);
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
		return user.getSignature();
	}

}
