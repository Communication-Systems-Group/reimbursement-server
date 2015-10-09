package ch.uzh.csg.reimbursement.service;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.HashSet;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import ch.uzh.csg.reimbursement.model.Expense;
import ch.uzh.csg.reimbursement.model.User;
import ch.uzh.csg.reimbursement.model.exception.AccessViolationException;
import ch.uzh.csg.reimbursement.model.exception.ExpenseNotFoundException;
import ch.uzh.csg.reimbursement.repository.ExpenseRepositoryProvider;

@RunWith(MockitoJUnitRunner.class)
public class ExpenseServiceTest {

	@InjectMocks
	private ExpenseService service;

	@Mock
	private ExpenseRepositoryProvider expenseRepository;

	@Mock
	private UserService userService;

	@Mock
	private UserResourceAuthorizationService authorizationService;

	@Captor
	private ArgumentCaptor<Expense> argumentCaptorExpense;

	@Test
	public void testCreateExpenseExpense() {

		// given
		String accounting = "Accounting";
		User user = mock(User.class);
		given(userService.getLoggedInUser()).willReturn(user);

		// when
		service.createExpense(accounting);

		// then
		verify(expenseRepository).create(argumentCaptorExpense.capture());

		Expense expense = argumentCaptorExpense.getValue();
		assertThat(expense.getAccounting(), is(equalTo(accounting)));
		assertThat(expense.getUser(), is(equalTo(user)));

	}

	@Test
	public void testGetAllByUser() {

		// given
		String uid = "user-uid";
		Set<Expense> expenseSet = new HashSet<Expense>();

		given(expenseRepository.findAllByUser(uid)).willReturn(expenseSet);

		// when
		Set<Expense> returningExpenseSet = expenseRepository.findAllByUser(uid);

		// then
		verify(expenseRepository).findAllByUser(uid);
		assertThat(returningExpenseSet, is(equalTo(expenseSet)));

	}

	@Ignore
	@Test
	public void testGetByUidCaseTrue() {

		// given
		String uid = "expense-uid";
		Expense expense = mock(Expense.class);

		given(authorizationService.checkViewAuthorization(expense)).willReturn(TRUE);
		given(expenseRepository.findByUid(uid)).willReturn(expense);

		// when
		Expense returningExpense = service.getByUid(uid);

		// then
		assertThat(returningExpense, is(equalTo(expense)));
	}

	@Ignore
	@Test(expected=AccessViolationException.class)
	public void testGetByUidCaseElse() {

		// given
		String uid = "expense-uid";
		Expense expense = mock(Expense.class);

		given(authorizationService.checkViewAuthorization(expense)).willReturn(FALSE);
		given(expenseRepository.findByUid(uid)).willReturn(expense);

		// when
		service.getByUid(uid);

		// then
		// throw AccessViolationException
	}

	@Ignore
	@Test(expected=ExpenseNotFoundException.class)
	public void testGetByUidCaseException() {

		// given
		String uid = "expense-uid";

		// when
		service.getByUid(uid);

		// then
		// throw ExpenseNotFoundException
	}
}
