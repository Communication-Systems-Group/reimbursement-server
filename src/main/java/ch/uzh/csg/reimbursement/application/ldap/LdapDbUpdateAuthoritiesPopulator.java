package ch.uzh.csg.reimbursement.application.ldap;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.naming.Name;

import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;

import ch.uzh.csg.reimbursement.model.UserAdapter;

public class LdapDbUpdateAuthoritiesPopulator implements LdapAuthoritiesPopulator {

	private UserDetailsService userDetailsService;

	public LdapDbUpdateAuthoritiesPopulator(UserDetailsService userDetailsService){
		this.userDetailsService = userDetailsService;
	}

	@Override
	public Collection<? extends GrantedAuthority> getGrantedAuthorities(DirContextOperations userData, String uid) {
		//user the method already in user to set the roles for the user
		Name dnName = userData.getDn();
		Set<String> dn = new HashSet<String>();
		for(int i=0; i<dnName.size(); i++) {
			dn.add(dnName.get(i));
		}
		UserAdapter user = (UserAdapter) userDetailsService.loadUserByUsername(uid);
		user.setRoles(dn);

		//get the authorities based on the user's roles from the UserAdapter and populate ldap with it
		return user.getAuthorities();
	}

}