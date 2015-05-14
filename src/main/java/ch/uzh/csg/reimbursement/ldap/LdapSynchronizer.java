package ch.uzh.csg.reimbursement.ldap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.CommunicationException;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ch.uzh.csg.reimbursement.service.UserService;

@Component
public class LdapSynchronizer {

	@Autowired
	private LdapTemplate ldapTemplate;

	@Autowired
	private UserService userService;

	private final Logger logger = LoggerFactory.getLogger(LdapSynchronizer.class);

	@Scheduled(fixedRateString = "${reimbursement.ldap.refreshRate}")
	public void synchronizeDomainWithLdap() {
		userService.synchronize(getLdapPersons());
	}

	private List<LdapPerson> getLdapPersons() {
		LdapPersonAttributesMapper mapper = new LdapPersonAttributesMapper();
		List<LdapPerson> list = new ArrayList<LdapPerson>();
		try {
			list = ldapTemplate.search("ou=People", "(&(objectClass=hostObject)(objectClass=inetOrgPerson))", mapper);
			list.removeAll(Collections.singleton(null));
		}
		catch(CommunicationException ex) {
			logger.error("Could not connect to the LDAP server. Check the connection.", ex);
		}
		return list;
	}

}
