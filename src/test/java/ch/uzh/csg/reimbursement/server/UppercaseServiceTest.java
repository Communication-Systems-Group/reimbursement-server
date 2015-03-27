package ch.uzh.csg.reimbursement.server;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.logging.Logger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UppercaseServiceTest {

	@InjectMocks
	private UppercaseService service;

	@Mock
	private Person person;

	@Mock
	private Logger logger;

	@Test
	public void testGetFirstNameInUppercase() {

		// given
		String firstName = "Franz";
		String firstNameUppercase = "FRANZ";

		given(person.getFirstName()).willReturn(firstName);

		// when
		String result = service.getFirstNameInUppercase();

		// then
		assertThat(result, is(equalTo(firstNameUppercase)));

	}

	@Test
	public void testGetConcatedNameInUppercase() {

		// given
		String firstName = "Peter";
		String lastName = "Meier";
		String expectedResult = "PETER MEIER";

		given(person.getFirstName()).willReturn(firstName);
		given(person.getLastName()).willReturn(lastName);

		// when
		String result = service.getConcatedNameInUppercase();

		// then
		assertThat(result, is(equalTo(expectedResult)));

	}

	@Test
	public void testLogLastName() {

		// given
		String lastName = "Müller";

		given(person.getLastName()).willReturn(lastName);

		// when
		service.logLastName();

		// then
		verify(logger).info(lastName);
	}

}