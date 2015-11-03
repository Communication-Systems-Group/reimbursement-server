package ch.uzh.csg.reimbursement.application.ldap;

import static ch.uzh.csg.reimbursement.model.Role.DEPUTY;
import static ch.uzh.csg.reimbursement.model.Role.PROF;

import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.support.AbstractContextMapper;

public class LdapMapper extends AbstractContextMapper<LdapPerson> {

	private final Logger LOG = LoggerFactory.getLogger(LdapMapper.class);

	@Override
	protected LdapPerson doMapFromContext(DirContextOperations ctx) {
		LdapPerson ldapPerson = new LdapPerson();
		Attributes attributes = ctx.getAttributes();

		if (attributes.get("sn") != null && attributes.get("givenName") != null && attributes.get("uid") != null
				&& attributes.get("mail") != null) {

			try {
				ldapPerson.setLastName(attributes.get("sn").get().toString());
				ldapPerson.setFirstName(attributes.get("givenName").get().toString());
				ldapPerson.setUid(attributes.get("uid").get().toString());
				ldapPerson.setEmail(attributes.get("mail").get().toString());

				if(attributes.get("manager") != null) {
					String manager = attributes.get("manager").get().toString();
					manager = manager.split("uid=")[1];
					if (manager != null) {
						manager = manager.split(",")[0];
					}
					ldapPerson.setManager(manager);
				}

				// the role is in the path (dn) to the user
				Name dnName = ctx.getDn();
				for(int i=0; i<dnName.size(); i++) {
					String dn = dnName.get(i);
					if("ou=Professors".equals(dn)) {
						ldapPerson.addRole(PROF);
						//TODO The finance admin should have the possibility to set the department manager in the settings
						ldapPerson.setManager("lauber");
					}
					if("ou=SeniorAssistants".equals(dn)) {
						ldapPerson.addRole(DEPUTY);
					}
				}
			}
			catch(NamingException ex) {
				LOG.warn("NamingException occured while synchronizing with LDAP.", ex);
			}

			return ldapPerson;
		} else {
			LOG.warn("Could not map LDAP person to Java.");
			return null;

		}
	}

}
