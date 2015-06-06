package ch.uzh.csg.reimbursement.application.ldap;

import java.util.HashSet;
import java.util.Set;

import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.support.AbstractContextMapper;

public class LdapMapper extends AbstractContextMapper<LdapPerson> {

	private final Logger logger = LoggerFactory.getLogger(LdapMapper.class);

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
				Set<String> dn = new HashSet<String>();
				for(int i=0; i<dnName.size(); i++) {
					dn.add(dnName.get(i));
				}
				ldapPerson.setDn(dn);
			}
			catch(NamingException ex) {
				logger.warn("NamingException occured while synchronizing with LDAP.", ex);
			}

			return ldapPerson;
		} else {
			logger.warn("Could not map LDAP person to Java.");
			return null;

		}
	}

}
