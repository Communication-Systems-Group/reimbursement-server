package ch.uzh.csg.reimbursement.application.ldap;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.support.AbstractContextMapper;

public class LdapMapperContactPerson extends AbstractContextMapper<String> {

	private final Logger logger = LoggerFactory.getLogger(LdapMapperContactPerson.class);

	@Override
	protected String doMapFromContext(DirContextOperations ctx) {
		Attributes attributes = ctx.getAttributes();
		String memberName = null;

		try {
			if (attributes.get("memberUid") != null) {
				memberName = attributes.get("memberUid").get().toString();
			} else {
				logger.warn("Could not find any member of Finance-Admin.");
				return null;
			}
		}
		catch(NamingException ex) {
			logger.warn("NamingException occured while synchronizing with LDAP.", ex);
		}

		return memberName;
	}

}
