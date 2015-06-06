package ch.uzh.csg.reimbursement.application.ldap;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.AttributesMapper;

public class LdapMapper implements AttributesMapper<LdapPerson> {

	private final Logger logger = LoggerFactory.getLogger(LdapMapper.class);

	@Override
	public LdapPerson mapFromAttributes(Attributes attributes) throws NamingException {
		LdapPerson ldapPerson = new LdapPerson();

		if (attributes.get("sn") != null && attributes.get("givenName") != null && attributes.get("uid") != null
				&& attributes.get("mail") != null) {

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

			return ldapPerson;

		} else {
			logger.warn("Could not map LDAP person to Java.");
			return null;
		}
	}

}
