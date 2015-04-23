package ch.uzh.csg.reimbursement.server.model;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Signature")
public class Signature {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private int id;

	@Setter
	@Column(nullable = false, updatable = true, unique = false, name = "content_type")
	private String contentType;

	@Setter
	@Column(nullable = false, updatable = true, unique = false, name = "file_size")
	private long fileSize;

	@Getter
	@Setter
	@Column(nullable = false, updatable = true, unique = false, name = "content", columnDefinition = "blob")
	private byte[] content;

	public Signature(String contentType, long fileSize, byte[] content) {
		setContentType(contentType);
		setFileSize(fileSize);
		setContent(content);
	}

	/*
	 * The default constructor is needed by Hibernate, but should not be used at all.
	 */
	protected Signature() {

	}
}
