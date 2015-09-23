package ch.uzh.csg.reimbursement.application.ldap;

import static ch.uzh.csg.reimbursement.model.Role.FINANCE_ADMIN;
import static ch.uzh.csg.reimbursement.model.Role.PROF;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.util.ReflectionTestUtils.getField;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import ch.uzh.csg.reimbursement.model.Role;

@RunWith(MockitoJUnitRunner.class)
public class LdapPersonTest {

	@Test
	public void testRemoveRoleWithExistingRole() {
		// given
		LdapPerson ldapPerson = new LdapPerson();
		Set<Role> roles = new HashSet<>();
		roles.add(PROF);
		setField(ldapPerson, "roles", roles);

		// when
		ldapPerson.removeRole(PROF);

		// then
		@SuppressWarnings("unchecked")
		Set<Role> afterRoles = (Set<Role>) getField(ldapPerson, "roles");
		assertThat(afterRoles.size(), is(equalTo(0)));
		assertThat(afterRoles.contains(PROF), is(equalTo(FALSE)));
	}

	@Test
	public void testRemoveRoleWithoutExistingRole() {
		// given
		LdapPerson ldapPerson = new LdapPerson();
		Set<Role> roles = new HashSet<>();
		roles.add(FINANCE_ADMIN);
		setField(ldapPerson, "roles", roles);

		// when
		ldapPerson.removeRole(PROF);

		// then
		@SuppressWarnings("unchecked")
		Set<Role> afterRoles = (Set<Role>) getField(ldapPerson, "roles");
		assertThat(afterRoles.size(), is(equalTo(1)));
		assertThat(afterRoles.contains(FINANCE_ADMIN), is(equalTo(TRUE)));
		assertThat(afterRoles.contains(PROF), is(equalTo(FALSE)));

	}

}
