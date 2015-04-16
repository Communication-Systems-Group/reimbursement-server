package ch.uzh.csg.reimbursement.server.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.csg.reimbursement.server.dao.UserDao;
import ch.uzh.csg.reimbursement.server.model.User;

@Transactional
@Service("userService")
public class UserServiceImpl implements UserService {

	@Autowired
	private UserDao dao;

	@Override
	public void saveUser(User user) {
		dao.saveUser(user);
	}

	@Override
	public List<User> findAllUsers() {
		return dao.findAllUsers();
	}

	@Override
	public void deleteUserByUid(String uid) {
		dao.deleteUserByUid(uid);
	}

	@Override
	public User findByUid(String uid) {
		return dao.findByUid(uid);
	}

	@Override
	public void updateUser(User user) {
		dao.updateUser(user);
	}

}
