package ch.uzh.csg.reimbursement.view;

import org.springframework.stereotype.Service;

import ch.uzh.csg.reimbursement.model.User;

@Service
public class UserMapper {

	public UserView mapUser(User user) {
		UserView mappedUser = new UserView();
		mappedUser.setUid(user.getUid());
		mappedUser.setFirstname(user.getFirstName());
		mappedUser.setLastname(user.getLastName());
		return mappedUser;
	}
}
