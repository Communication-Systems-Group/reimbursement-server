package ch.uzh.csg.reimbursement.dto;

import lombok.Value;


@Value
public class AttachmentPdfDto implements IPdfDto {

	private String base64String;

}
