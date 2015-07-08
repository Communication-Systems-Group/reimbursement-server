package ch.uzh.csg.reimbursement.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@SuppressWarnings("serial")
public class UserAdapter implements UserDetails {
	private static final Logger LOGGER = LoggerFactory.getLogger(UserAdapter.class);

	private Collection<GrantedAuthority> authorities;
	private User user;

	public UserAdapter(User user){
		this.user = user;
		authorities = new HashSet<GrantedAuthority>();
		Set<Role> roles = user.getRoles();
		for(Role role : roles){
			authorities.add(new SimpleGrantedAuthority(role.name()));
			LOGGER.info("Added Role:"+role.name()+" to uid:"+user.getUid());

		}
	}
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return null;
	}

	@Override
	public String getUsername() {
		return user.getUid();
	}

	@Override
	public boolean isAccountNonExpired() {
		return false;
	}

	@Override
	public boolean isAccountNonLocked() {
		return false;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return false;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
