package ch.uzh.csg.reimbursement.server.dao;

import java.util.List;

import ch.uzh.csg.reimbursement.server.model.User;

public interface UserDao {

	void saveUser(User user);

	List<User> findAllUsers();

	void deleteUserByUid(String uid);

	User findByUid(String uid);

	void updateUser(User user);

}
