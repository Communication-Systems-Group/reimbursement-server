package ch.uzh.csg.reimbursement.server.service;

import java.util.List;

import ch.uzh.csg.reimbursement.server.model.User;

public interface UserService {

	void saveUser(User user);

	List<User> findAllUsers();

	void deleteUserByUid(String uid);

	User findByUid(String uid);

	void updateUser(User user);

}
