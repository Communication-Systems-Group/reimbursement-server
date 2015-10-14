package ch.uzh.csg.reimbursement.model;

import static java.util.UUID.randomUUID;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.uzh.csg.reimbursement.view.View;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "Document_")
public class Document {

	@Transient
	private final Logger LOG = LoggerFactory.getLogger(Document.class);

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private int id;

	@JsonView(View.SummaryWithUid.class)
	@Getter
	@Setter
	@Column(nullable = false, updatable = true, unique = false, name = "uid")
	private String uid;

	@JsonProperty("type")
	@Getter
	@Column(nullable = false, updatable = true, unique = false, name = "content_type")
	private String contentType;

	@Column(nullable = false, updatable = false, unique = false, name = "type")
	@Enumerated(STRING)
	private DocumentType documentType;

	@JsonIgnore
	@Getter
	@Column(nullable = false, updatable = true, unique = false, name = "file_size")
	private long fileSize;

	@Getter
	@Setter
	@Column(nullable = false, updatable = true, unique = false, name = "content", columnDefinition = "blob")
	private byte[] content;

	public Document(String contentType, long fileSize, byte[] content, DocumentType documentType) {
		this.uid = randomUUID().toString();
		this.contentType = contentType;
		this.fileSize = fileSize;
		this.content = content;
		this.documentType = documentType;
		LOG.info("Document constructor: Document created");
	}

	public void updateDocument(String contentType, long fileSize, byte[] content) {
		this.contentType = contentType;
		this.fileSize = fileSize;
		this.content = content;
		LOG.info("Document updated: Document created");
	}

	/*
	 * The default constructor is needed by Hibernate, but should not be used at
	 * all.
	 */
	protected Document() {
	}
}
