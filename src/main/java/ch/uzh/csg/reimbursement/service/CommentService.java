package ch.uzh.csg.reimbursement.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.uzh.csg.reimbursement.dto.CommentDto;
import ch.uzh.csg.reimbursement.model.Comment;
import ch.uzh.csg.reimbursement.model.ExpenseItem;
import ch.uzh.csg.reimbursement.repository.CommentRepositoryProvider;

@Service
@Transactional
public class CommentService {

	@Autowired
	CommentRepositoryProvider commentRepository;

	@Autowired
	UserService userService;

	@Autowired
	private ExpenseItemService expenseItemService;

	public String createExpenseItemComment(String uid, CommentDto dto) {
		ExpenseItem expenseItem = expenseItemService.findByUid(uid);
		Comment comment = new Comment(new Date(), userService.getLoggedInUser(), expenseItem, dto.getText());
		commentRepository.create(comment);
		return comment.getUid();
	}
}
