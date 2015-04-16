package ch.uzh.csg.reimbursement.server.example;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PersonServiceTest {

	@InjectMocks
	private PersonService personService;

	@Mock
	private Person person;

	@Mock
	private UppercaseService uppercaseService;

	@Test
	public void testGetFirstNameInUppercase() {

		// given
		String firstName = "Franz";
		String firstNameUppercase = "FRANZ";

		given(person.getFirstName()).willReturn(firstName);
		given(uppercaseService.up(firstName)).willReturn(firstNameUppercase);

		// when
		String result = personService.getFirstNameInUppercase();

		// then
		assertThat(result, is(equalTo(firstNameUppercase)));

	}

	@Test
	public void testGetConcatedNameInUppercase() {

		// given
		String firstName = "Peter";
		String firstNameUppercase = "PETER";
		String lastName = "Meier";
		String lastNameUppercase = "MEIER";
		String expectedResult = "PETER MEIER";

		given(person.getFirstName()).willReturn(firstName);
		given(person.getLastName()).willReturn(lastName);
		given(uppercaseService.up(firstName)).willReturn(firstNameUppercase);
		given(uppercaseService.up(lastName)).willReturn(lastNameUppercase);

		// when
		String result = personService.getConcatedNameInUppercase();

		// then
		assertThat(result, is(equalTo(expectedResult)));

	}

	@Test
	public void testSayHelloToPerson() {

		// given

		// when
		personService.sayHelloToPerson();

		// then
		verify(person).sayHello();
	}

}