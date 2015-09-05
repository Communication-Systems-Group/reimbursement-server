package ch.uzh.csg.reimbursement.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.csg.reimbursement.model.User;

@Service
public class UserRepositoryProvider {

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
