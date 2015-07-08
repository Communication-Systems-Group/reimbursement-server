package ch.uzh.csg.reimbursement.application.ldap;

import java.util.Collection;
import java.util.HashSet;

import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;

public class LdapUserDetailsAuthoritiesPopulator implements LdapAuthoritiesPopulator {

	private final UserDetailsService userDetailsService;

	public LdapUserDetailsAuthoritiesPopulator(UserDetailsService userService) {
		this.userDetailsService = userService;
	}

	@Override
	public Collection<? extends GrantedAuthority> getGrantedAuthorities(DirContextOperations userData, String uid) {
		Collection<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
		UserDetails userDetails = userDetailsService.loadUserByUsername(uid);
		authorities.addAll(userDetails.getAuthorities());
		return authorities;
	}

}
