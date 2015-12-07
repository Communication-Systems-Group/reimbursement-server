package ch.uzh.csg.reimbursement.dto;

import java.util.Date;

import lombok.Value;


@Value
public class AttachmentCoverPdfDto implements IPdfDto {

	private Date date;
	private String costCategory;
	private int accountNumber;
	private String explanation;
	private double amount;
	private String project;
	private int iterator;

}
