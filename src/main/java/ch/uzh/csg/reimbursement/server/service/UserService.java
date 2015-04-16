package ch.uzh.csg.reimbursement.server.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.csg.reimbursement.server.model.User;
import ch.uzh.csg.reimbursement.server.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	public void create(User user) {
		userRepository.save(user);
	}

	public void delete(User user) {
		userRepository.delete(user);
	}

	public List<User> findAll() {
		return userRepository.findAll();
	}

	public User findByUid(String uid) {
		return userRepository.findByUid(uid);
	}

}
