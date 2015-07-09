package ch.uzh.csg.reimbursement.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@Table(name = "ExpenseItemAttachment")
public class ExpenseItemAttachment {

	@Transient
	private final Logger LOG = LoggerFactory.getLogger(ExpenseItemAttachment.class);

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private int id;

	@Getter
	@Setter
	@Column(nullable = false, updatable = true, unique = false, name = "uid")
	private String uid;

	@Getter
	@Setter
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "expense_item_id")
	private ExpenseItem expenseItem;

	@Column(nullable = false, updatable = true, unique = false, name = "content_type")
	private String contentType;

	@Column(nullable = false, updatable = true, unique = false, name = "file_size")
	private long fileSize;

	@Getter
	@Setter
	@Column(nullable = false, updatable = true, unique = false, name = "content", columnDefinition = "blob")
	private byte[] content;

	public ExpenseItemAttachment(String contentType, long fileSize, byte[] content, ExpenseItem expenseItem) {
		this.uid = UUID.randomUUID().toString();
		this.contentType = contentType;
		this.fileSize = fileSize;
		this.content = content;
		this.expenseItem = expenseItem;
		LOG.info("ExpenseItemAttachment constructor: ExpenseItemAttachment created");
	}

	public void updateExpenseItemAttachment(String contentType, long fileSize, byte[] content) {
		this.uid = UUID.randomUUID().toString();
		this.contentType = contentType;
		this.fileSize = fileSize;
		this.content = content;
		LOG.info("ExpenseItemAttachment updated: ExpenseItemAttachment created");
	}

	/*
	 * The default constructor is needed by Hibernate, but should not be used at all.
	 */
	protected ExpenseItemAttachment() {
	}
}
