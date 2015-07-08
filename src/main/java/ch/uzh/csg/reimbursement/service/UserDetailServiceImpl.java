package ch.uzh.csg.reimbursement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import ch.uzh.csg.reimbursement.model.User;
import ch.uzh.csg.reimbursement.model.UserAdapter;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

	@Autowired
	private UserService userService;

	@Override
	public UserDetails loadUserByUsername(String uid) throws UsernameNotFoundException {
		User user = userService.findByUid(uid);
		UserDetails userDetails = new UserAdapter(user);
		return userDetails;
	}

}
