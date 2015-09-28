package ch.uzh.csg.reimbursement.model;

import static java.util.UUID.randomUUID;
import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name = "ExpensePdf")
public class ExpensePdf {

	@Transient
	private final Logger LOG = LoggerFactory.getLogger(ExpensePdf.class);

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

	@JsonIgnore
	@Getter
	@Column(nullable = false, updatable = true, unique = false, name = "file_size")
	private long fileSize;

	@Getter
	@Setter
	@Column(nullable = false, updatable = true, unique = false, name = "content", columnDefinition = "blob")
	private byte[] content;

	public ExpensePdf(String contentType, long fileSize, byte[] content) {
		this.uid = randomUUID().toString();
		this.contentType = contentType;
		this.fileSize = fileSize;
		this.content = content;
		LOG.info("ExpensePdf constructor: ExpensePdf created");
	}

	public void updateExpensePdf(String contentType, long fileSize, byte[] content) {
		this.contentType = contentType;
		this.fileSize = fileSize;
		this.content = content;
		LOG.info("ExpensePdf updated: ExpensePdf created");
	}

	/*
	 * The default constructor is needed by Hibernate, but should not be used at
	 * all.
	 */
	protected ExpensePdf() {
	}
}
