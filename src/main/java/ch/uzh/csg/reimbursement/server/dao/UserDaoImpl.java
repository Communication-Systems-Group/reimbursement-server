package ch.uzh.csg.reimbursement.server.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import ch.uzh.csg.reimbursement.server.model.User;

@Repository("userDao")
public class UserDaoImpl extends AbstractDao implements UserDao {

	@Override
	public void saveUser(User user) {
		persist(user);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<User> findAllUsers() {
		Criteria criteria = getSession().createCriteria(User.class);
		return criteria.list();
	}

	@Override
	public void deleteUserByUid(String uid) {
		Query query = getSession().createSQLQuery("delete from User where uid = :uid");
		query.setString("uid", uid);
		query.executeUpdate();
	}

	@Override
	public User findByUid(String uid) {
		Criteria criteria = getSession().createCriteria(User.class);
		criteria.add(Restrictions.eq("uid", uid));
		return (User) criteria.uniqueResult();
	}

	@Override
	public void updateUser(User user) {
		getSession().update(user);
	}

}
