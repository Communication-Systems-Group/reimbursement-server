package ch.uzh.csg.reimbursement.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.uzh.csg.reimbursement.dto.CommentDto;
import ch.uzh.csg.reimbursement.model.Comment;
import ch.uzh.csg.reimbursement.model.Expense;
import ch.uzh.csg.reimbursement.repository.CommentRepositoryProvider;

@Service
@Transactional
public class CommentService {

	@Autowired
	private CommentRepositoryProvider commentRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private ExpenseService expenseService;

	public Comment createExpenseComment(String uid, CommentDto dto) {
		Expense expense = expenseService.findByUid(uid);
		Comment comment = new Comment(new Date(), userService.getLoggedInUser(), expense, dto.getText());
		commentRepository.create(comment);
		return comment;
	}
}