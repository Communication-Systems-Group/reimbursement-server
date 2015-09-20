package ch.uzh.csg.reimbursement.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import ch.uzh.csg.reimbursement.dto.CommentDto;
import ch.uzh.csg.reimbursement.model.Comment;
import ch.uzh.csg.reimbursement.model.Expense;
import ch.uzh.csg.reimbursement.model.User;
import ch.uzh.csg.reimbursement.repository.CommentRepositoryProvider;

@RunWith(MockitoJUnitRunner.class)
public class CommentServiceTest {

	@InjectMocks
	private CommentService service;

	@Mock
	private CommentRepositoryProvider commentRepository;

	@Mock
	private UserService userService;

	@Captor
	private ArgumentCaptor<Comment> argumentCaptorComment;

	@Test
	public void testCreateExpenseComment() throws Exception {

		// given
		CommentDto dto = mock(CommentDto.class);
		given(dto.getText()).willReturn("Text");

		User user = mock(User.class);
		Expense expense = mock(Expense.class);
		given(userService.getLoggedInUser()).willReturn(user);

		// when
		service.createExpenseComment(expense, dto);

		// then
		verify(commentRepository).create(argumentCaptorComment.capture());

		Comment comment = argumentCaptorComment.getValue();
		assertThat(comment.getText(), is(equalTo(dto.getText())));

	}

}
