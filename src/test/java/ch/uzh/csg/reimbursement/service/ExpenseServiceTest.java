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
import ch.uzh.csg.reimbursement.model.ExpenseState;
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
	public void testCreateExpense() {

		// given
		String accounting = "Accounting";
		User user = mock(User.class);
		given(userService.getLoggedInUser()).willReturn(user);

		// when
		service.create(accounting);

		// then
		verify(expenseRepository).create(argumentCaptorExpense.capture());

		Expense expense = argumentCaptorExpense.getValue();
		assertThat(expense.getAccounting(), is(equalTo(accounting)));
		assertThat(expense.getUser(), is(equalTo(user)));

	}

	@Test
	public void testFindAllByUser() {

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

	@Test
	public void testFindByUidCaseTrue() {

		// given
		String uid = "expense-uid";
		Expense expense = mock(Expense.class);

		given(authorizationService.checkViewAuthorization(expense)).willReturn(TRUE);
		given(expenseRepository.findByUid(uid)).willReturn(expense);

		// when
		Expense returningExpense = service.findByUid(uid);

		// then
		assertThat(returningExpense, is(equalTo(expense)));
	}

	@Test(expected=AccessViolationException.class)
	public void testFindByUidCaseElse() {

		// given
		String uid = "expense-uid";
		Expense expense = mock(Expense.class);

		given(authorizationService.checkViewAuthorization(expense)).willReturn(FALSE);
		given(expenseRepository.findByUid(uid)).willReturn(expense);

		// when
		service.findByUid(uid);

		// then
		// throw AccessViolationException
	}

	@Ignore
	@Test(expected=ExpenseNotFoundException.class)
	public void testFindByUidCaseException() {

		// given
		String uid = "expense-uid";

		// when
		service.findByUid(uid);

		// then
		// throw ExpenseNotFoundException
	}

	@Test
	public void testFindAllByState() {
		// given
		ExpenseState state = ExpenseState.DRAFT;
		Set<Expense> expenseSet = new HashSet<Expense>();

		given(expenseRepository.findAllByState(state)).willReturn(expenseSet);

		// when
		Set<Expense> returningExpenseSet = expenseRepository.findAllByState(state);

		// then
		verify(expenseRepository).findAllByState(state);
		assertThat(returningExpenseSet, is(equalTo(expenseSet)));
	}
}
