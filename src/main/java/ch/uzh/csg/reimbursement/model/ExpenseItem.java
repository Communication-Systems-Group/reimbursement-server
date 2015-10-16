package ch.uzh.csg.reimbursement.model;

import static ch.uzh.csg.reimbursement.model.DocumentType.ATTACHMENT;
import static ch.uzh.csg.reimbursement.model.ExpenseItemState.INITIAL;
import static ch.uzh.csg.reimbursement.model.ExpenseItemState.SUCCESFULLY_CREATED;
import static javax.persistence.CascadeType.ALL;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import ch.uzh.csg.reimbursement.dto.ExpenseItemDto;
import ch.uzh.csg.reimbursement.model.exception.AttachmentNotFoundException;
import ch.uzh.csg.reimbursement.model.exception.MaxFileSizeViolationException;
import ch.uzh.csg.reimbursement.model.exception.MinFileSizeViolationException;
import ch.uzh.csg.reimbursement.model.exception.ServiceException;
import ch.uzh.csg.reimbursement.serializer.ExpenseSerializer;
import ch.uzh.csg.reimbursement.utils.PropertyProvider;
import ch.uzh.csg.reimbursement.view.View.SummaryWithUid;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
@Table(name = "ExpenseItem_")
@Transactional
@JsonIgnoreProperties({ "expenseItemAttachment" })
public class ExpenseItem {

	@Transient
	private final Logger LOG = LoggerFactory.getLogger(ExpenseItem.class);

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private int id;

	@JsonView(SummaryWithUid.class)
	@Getter
	@Column(nullable = false, updatable = true, unique = false, name = "uid")
	private String uid;

	@JsonSerialize(using = ExpenseSerializer.class)
	@Getter
	@ManyToOne(optional = false, cascade = { CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE })
	@JoinColumn(name = "expense_id")
	private Expense expense;

	@Getter
	@Column(nullable = false, updatable = true, unique = false, name = "date")
	private Date date;

	@Getter
	@Enumerated(STRING)
	@Column(nullable = false, updatable = true, unique = false, name = "state")
	private ExpenseItemState state;

	@Getter
	@Column(nullable = true, updatable = true, unique = false, name = "original_amount")
	private double originalAmount;

	@Getter
	@Column(nullable = true, updatable = true, unique = false, name = "calculated_amount")
	private double calculatedAmount;

	@Getter
	@ManyToOne(optional = false, cascade = { CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE })
	@JoinColumn(name = "cost_category_id")
	private CostCategory costCategory;

	@Getter
	@Column(nullable = true, updatable = true, unique = false, name = "explanation")
	private String explanation;

	@Getter
	@Column(nullable = true, updatable = true, unique = false, name = "currency")
	private String currency;

	@JsonIgnore
	@Getter
	@Column(nullable = true, updatable = true, unique = false, name = "exchange_rate")
	private double exchangeRate;

	@Getter
	@Column(nullable = true, updatable = true, unique = false, name = "project")
	private String project;

	@JsonIgnore
	@OneToOne(cascade = ALL, orphanRemoval = true)
	@JoinColumn(name = "document_id")
	private Document attachment;

	public Document setAttachment(MultipartFile multipartFile) {
		// TODO remove PropertyProvider and replace it with @Value values in the
		// calling class of this method.
		// you can find examples in the method Token.isExpired.
		if (multipartFile.getSize() <= Long.parseLong(PropertyProvider.INSTANCE
				.getProperty("reimbursement.filesize.minUploadFileSize"))) {
			LOG.error("File to small, allowed: "
					+ PropertyProvider.INSTANCE.getProperty("reimbursement.filesize.minUploadFileSize")
					+ " actual: " + multipartFile.getSize());
			throw new MinFileSizeViolationException();
		} else if (multipartFile.getSize() >= Long.parseLong(PropertyProvider.INSTANCE
				.getProperty("reimbursement.filesize.maxUploadFileSize"))) {
			LOG.error("File to big, allowed: "
					+ PropertyProvider.INSTANCE.getProperty("reimbursement.filesize.maxUploadFileSize")
					+ " actual: " + multipartFile.getSize());
			throw new MaxFileSizeViolationException();
		} else {
			byte[] content = null;
			try {
				content = multipartFile.getBytes();
				attachment = new Document(multipartFile.getContentType(),
						multipartFile.getSize(), content, ATTACHMENT);
			} catch (IOException e) {
				LOG.error("An IOException has been caught while creating a signature.", e);
				throw new ServiceException();
			}
		}
		return attachment;
	}

	public Document setAttachment(Document doc) {
		return attachment = doc;
	}

	public Document getAttachment() {
		if (attachment == null) {
			LOG.error("No attachment found for the expenseItem with uid: " + this.uid);
			throw new AttachmentNotFoundException();
		}
		return attachment;
	}

	public ExpenseItem(CostCategory costCategory, double exchangeRate, double calculatedAmount, Expense expense,
			ExpenseItemDto dto) {
		this.uid = UUID.randomUUID().toString();
		this.state = INITIAL;
		this.date = dto.getDate();
		this.costCategory = costCategory;
		this.explanation = dto.getExplanation();
		this.currency = dto.getCurrency();
		this.exchangeRate = exchangeRate;
		this.originalAmount = dto.getOriginalAmount();
		this.calculatedAmount = calculatedAmount;
		this.project = dto.getProject();
		this.expense = expense;
		expense.updateExpense();
	}

	public void updateExpenseItem(CostCategory costCategory, double exchangeRate, double calculatedAmount,
			ExpenseItemDto dto) {
		this.state = SUCCESFULLY_CREATED;
		this.date = dto.getDate();
		this.costCategory = costCategory;
		this.explanation = dto.getExplanation();
		this.currency = dto.getCurrency();
		this.exchangeRate = exchangeRate;
		this.originalAmount = dto.getOriginalAmount();
		this.calculatedAmount = calculatedAmount;
		this.project = dto.getProject();
	}

	/*
	 * The default constructor is needed by Hibernate, but should not be used at
	 * all.
	 */
	protected ExpenseItem() {
	}
}
