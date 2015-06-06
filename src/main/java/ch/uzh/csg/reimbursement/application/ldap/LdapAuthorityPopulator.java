package ch.uzh.csg.reimbursement.application.ldap;

import java.util.Collection;
import java.util.HashSet;

import javax.naming.Name;

import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;

public class LdapAuthorityPopulator implements LdapAuthoritiesPopulator {

	@Override
	public Collection<? extends GrantedAuthority> getGrantedAuthorities(DirContextOperations userData, String uid) {
		Collection<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();

		// TODO replace this code with a database access to get the roles from there (with uid)
		Name dn = userData.getDn();
		for(int i=0; i<dn.size(); i++) {
			String group = dn.get(i);
			if(group.equals("ou=Professors")) {
				authorities.add(new SimpleGrantedAuthority("PROF"));
			}
			// extend the list here if you need to add more authorities
		}
		authorities.add(new SimpleGrantedAuthority("USER"));

		return authorities;
	}

}
