package ch.uzh.csg.reimbursement.application.ldap;

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

	private final Logger logger = LoggerFactory.getLogger(LdapSynchronizer.class);

	@Autowired
	private LdapTemplate ldapTemplate;

	@Autowired
	private UserService userService;

	@Scheduled(fixedRateString = "${reimbursement.ldap.refreshRate}")
	public void synchronizeDomainWithLdap() {
		userService.synchronize(getLdapPersons());
	}

	private List<LdapPerson> getLdapPersons() {
		LdapMapper mapper = new LdapMapper();
		LdapMapperContactPerson mapperContactPerson = new LdapMapperContactPerson();
		List<LdapPerson> list = new ArrayList<LdapPerson>();
		try {
			list = ldapTemplate.search("ou=People", "(&(objectClass=hostObject)(objectClass=inetOrgPerson))", mapper);
			list.removeAll(Collections.singleton(null));

			List<String> contactPersons = ldapTemplate.search("ou=Groups", "cn=finance-admin", mapperContactPerson);
			for(LdapPerson ldapPerson : list) {
				for(String contactPersonUid : contactPersons) {
					if(ldapPerson.getUid().equals(contactPersonUid)) {
						ldapPerson.getDn().add("ou=contactperson");
					}
				}
			}
		}
		catch(CommunicationException ex) {
			logger.error("Could not connect to the LDAP server. Check the connection.", ex);
		}
		return list;
	}

}
