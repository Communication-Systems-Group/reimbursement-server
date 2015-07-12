package ch.uzh.csg.reimbursement.view;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.csg.reimbursement.model.Comment;

@Service
public class NoteMapper {

	@Autowired
	private UserMapper userMapper;

	public Set<NoteView> mapNote(Set<Comment> comments) {
		Set<NoteView> mappedNotes = new HashSet<NoteView>();
		for(Comment comment: comments) {
			mappedNotes.add(mapNote(comment));
		}
		return mappedNotes;
	}

	public NoteView mapNote(Comment comment) {
		NoteView mappedNote = new NoteView();
		mappedNote.setDate(comment.getDate());
		mappedNote.setCreator(userMapper.mapUser(comment.getUser()));
		mappedNote.setText(comment.getText());
		return mappedNote;
	}
}
