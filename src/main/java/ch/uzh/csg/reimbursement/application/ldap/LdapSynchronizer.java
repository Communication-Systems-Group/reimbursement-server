package ch.uzh.csg.reimbursement.application.ldap;

import static ch.uzh.csg.reimbursement.configuration.BuildLevel.PRODUCTION;
import static ch.uzh.csg.reimbursement.model.Role.DEPARTMENT_MANAGER;
import static ch.uzh.csg.reimbursement.model.Role.FINANCE_ADMIN;
import static ch.uzh.csg.reimbursement.model.Role.PROF;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.CommunicationException;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ch.uzh.csg.reimbursement.configuration.BuildLevel;
import ch.uzh.csg.reimbursement.service.UserService;

@Component
public class LdapSynchronizer {

	private final Logger LOG = LoggerFactory.getLogger(LdapSynchronizer.class);

	@Autowired
	private LdapTemplate ldapTemplate;

	@Autowired
	private UserService userService;

	@Value("${reimbursement.buildLevel}")
	private BuildLevel buildLevel;

	@Scheduled(fixedRateString = "${reimbursement.ldap.refreshRate}")
	public void synchronizeDomainWithLdap() {
		userService.synchronize(getLdapPersons());
	}

	private List<LdapPerson> getLdapPersons() {
		LdapMapper mapper = new LdapMapper();
		LdapCommonNameMapper commonNameMapper = new LdapCommonNameMapper();
		List<LdapPerson> list = new ArrayList<LdapPerson>();
		try {
			list = ldapTemplate.search("ou=People", "(&(objectClass=hostObject)(objectClass=inetOrgPerson))", mapper);
			list.removeAll(Collections.singleton(null));

			List<String> financeAdmins = ldapTemplate.search("ou=Groups", "cn=finance-admin", commonNameMapper);
			List<String> departmentManagerList = ldapTemplate.search("ou=Groups", "cn=department-manager",
					commonNameMapper);

			if (departmentManagerList.size() > 1) {
				LOG.error("There should only be one department manager, if there are more than one presons in the list the first in the list will be set as department manager.");
			}
			String departmentManager = departmentManagerList.get(0);

			for (LdapPerson ldapPerson : list) {
				for (String financeAdminUid : financeAdmins) {
					if (ldapPerson.getUid().equals(financeAdminUid)) {
						ldapPerson.addRole(FINANCE_ADMIN);

						// a user cannot be prof and finance admin
						// remove prof role if it is there
						ldapPerson.removeRole(PROF);
					}
				}

				if (ldapPerson.getUid().equals(departmentManager) && buildLevel == PRODUCTION) {
					ldapPerson.addRole(DEPARTMENT_MANAGER);

					// a user cannot be department manager and finance admin or prof
					// remove finance admin and prof role if it is there
					ldapPerson.removeRole(PROF);
					ldapPerson.removeRole(FINANCE_ADMIN);
				}
			}

			// To assign the prof's and finance_admin's manager it has to be ensured that all users have the mentioned roles because the
			// assignment of the manager is dependent on these roles.
			// To ensure that another iteration over the LdapPerson list has to be done after the role assignment.
			for (LdapPerson ldapPerson : list) {
				if (ldapPerson.getRoles().contains(PROF)) {
					ldapPerson.setManager(departmentManager);
				}
				if (ldapPerson.getRoles().contains(FINANCE_ADMIN)) {
					ldapPerson.setManager(departmentManager);
				}
			}

		} catch (CommunicationException ex) {
			LOG.error("Could not connect to the LDAP server. Check the connection.", ex);
		}
		return list;
	}

}
