package ch.uzh.csg.reimbursement.view;

import java.util.Date;

import lombok.Data;

@Data
public class NoteView {
	private Date date;
	private UserView creator;
	private String text;
}