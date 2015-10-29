package ch.uzh.csg.reimbursement.application.ldap;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.support.AbstractContextMapper;

public class LdapCommonNameMapper extends AbstractContextMapper<String> {

	private final Logger LOG = LoggerFactory.getLogger(LdapCommonNameMapper.class);

	@Override
	protected String doMapFromContext(DirContextOperations ctx) {
		Attributes attributes = ctx.getAttributes();
		String memberName = null;

		try {
			if (attributes.get("memberUid") != null) {
				memberName = attributes.get("memberUid").get().toString();
			} else {
				LOG.warn("Could not find any member of the given common name.");
				return null;
			}
		}
		catch(NamingException ex) {
			LOG.warn("NamingException occured while synchronizing with LDAP.", ex);
		}

		return memberName;
	}

}
