package ch.uzh.csg.reimbursement.application.ldap;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;
import ch.uzh.csg.reimbursement.model.Role;

@Data
public class LdapPerson {

	private String firstName;
	private String lastName;
	private String uid;
	private String email;
	private String manager;
	private Set<Role> roles = new HashSet<>();

	public void addRole(Role role) {
		roles.add(role);
	}

	public void removeRole(Role role) {
		if(roles.contains(role)) {
			roles.remove(role);
		}
	}

}
