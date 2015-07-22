package ch.uzh.csg.reimbursement.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import ch.uzh.csg.reimbursement.dto.ExpenseDto;
import ch.uzh.csg.reimbursement.model.Expense;
import ch.uzh.csg.reimbursement.model.User;
import ch.uzh.csg.reimbursement.repository.ExpenseRepositoryProvider;

@RunWith(MockitoJUnitRunner.class)
public class ExpenseServiceTest {

	@InjectMocks
	private ExpenseService service;

	@Mock
	private ExpenseRepositoryProvider repository;

	@Mock
	private UserService userService;

	@Captor
	private ArgumentCaptor<Expense> argumentCaptorExpense;

	@Test
	public void testCreateExpense() {

		// given
		ExpenseDto dto = mock(ExpenseDto.class);
		given(dto.getAccounting()).willReturn("Accounting");

		User user = mock(User.class);
		given(userService.getLoggedInUser()).willReturn(user);

		// when
		service.create(dto);

		// then
		verify(repository).create(argumentCaptorExpense.capture());

		Expense expense = argumentCaptorExpense.getValue();
		assertThat(expense.getAccounting(), is(equalTo(dto.getAccounting())));
		assertThat(expense.getUser(), is(equalTo(user)));

	}

	@Test
	public void testFindAllByUser() {

		// given
		String uid = "user-uid";
		Set<Expense> expenseSet = new HashSet<Expense>();

		given(repository.findAllByUser(uid)).willReturn(expenseSet);

		// when
		Set<Expense> returningExpenseSet = repository.findAllByUser(uid);

		// then
		verify(repository).findAllByUser(uid);
		assertThat(returningExpenseSet, is(equalTo(expenseSet)));

	}

	@Test
	public void testFindByUid() {

		// given
		String uid = "expense-uid";
		Expense expense = mock(Expense.class);

		given(repository.findByUid(uid)).willReturn(expense);

		// when
		Expense returningExpense = service.findByUid(uid);

		// then
		verify(repository).findByUid(uid);
		assertThat(returningExpense, is(equalTo(expense)));
	}

}
