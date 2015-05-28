package ch.uzh.csg.reimbursement.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import ch.uzh.csg.reimbursement.model.User;
import ch.uzh.csg.reimbursement.model.exception.UserNotFoundException;
import ch.uzh.csg.reimbursement.repository.UserRepositoryProvider;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

	@InjectMocks
	private UserService service;

	@Mock
	private UserRepositoryProvider repository;

	@Test(expected = UserNotFoundException.class)
	public void testFindByUidIfNoUserIsFound() {

		// given
		String uid = "fancy-user-id";
		given(repository.findByUid(uid)).willReturn(null);

		// when
		service.findByUid(uid);

		// then
		// above mentioned exception is thrown
	}

	@Test
	public void testFindByUidIfAUserIsFound() {

		// given
		String uid = "fancy-user-id";
		User user = mock(User.class);
		given(repository.findByUid(uid)).willReturn(user);

		// when
		User returningUser = service.findByUid(uid);

		// then
		assertThat(returningUser, is(equalTo(user)));

	}

}
