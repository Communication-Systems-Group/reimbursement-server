package ch.uzh.csg.reimbursement.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.csg.reimbursement.model.Comment;

@Service
public class CommentRepositoryProvider {

	@Autowired
	private CommentRepository commentRepository;

	public void create(Comment comment) {
		commentRepository.save(comment);
	}

	public Comment findByUid(String uid) {
		return commentRepository.findByUid(uid);
	}
}
