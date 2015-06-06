package ch.uzh.csg.reimbursement.application.ldap;

import java.util.Set;

import lombok.Data;

@Data
public class LdapPerson {

	private String firstName;
	private String lastName;
	private String uid;
	private String email;
	private String manager;
	private Set<String> dn;

}
