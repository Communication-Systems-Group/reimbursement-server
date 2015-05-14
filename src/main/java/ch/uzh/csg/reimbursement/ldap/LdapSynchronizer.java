package ch.uzh.csg.reimbursement.ldap;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

	@SuppressWarnings("unchecked")
	private List<LdapPerson> getLdapPersons() {
		LdapPersonAttributesMapper mapper = new LdapPersonAttributesMapper();
		List<LdapPerson> list = ldapTemplate.search("ou=People", "(&(objectClass=hostObject)(objectClass=inetOrgPerson))", mapper);
		list.removeAll(Collections.singleton(null));
		return list;
	}

	@Scheduled(fixedRateString = "${reimbursement.ldap.refreshRate}")
	public void synchronizeDomainWithLdap() {
		userService.synchronize(getLdapPersons());
	}

}
