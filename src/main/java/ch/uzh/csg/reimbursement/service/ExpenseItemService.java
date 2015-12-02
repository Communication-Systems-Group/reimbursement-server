package ch.uzh.csg.reimbursement.service;

import static org.apache.xmlgraphics.util.MimeConstants.MIME_GIF;
import static org.apache.xmlgraphics.util.MimeConstants.MIME_JPEG;
import static org.apache.xmlgraphics.util.MimeConstants.MIME_PDF;
import static org.apache.xmlgraphics.util.MimeConstants.MIME_PNG;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import ch.uzh.csg.reimbursement.application.validation.ValidationService;
import ch.uzh.csg.reimbursement.dto.ExchangeRateDto;
import ch.uzh.csg.reimbursement.dto.ExpenseItemDto;
import ch.uzh.csg.reimbursement.dto.ExpenseItemPdfDto;
import ch.uzh.csg.reimbursement.model.CostCategory;
import ch.uzh.csg.reimbursement.model.Document;
import ch.uzh.csg.reimbursement.model.Expense;
import ch.uzh.csg.reimbursement.model.ExpenseItem;
import ch.uzh.csg.reimbursement.model.Token;
import ch.uzh.csg.reimbursement.model.exception.AccessException;
import ch.uzh.csg.reimbursement.model.exception.ExpenseItemNotFoundException;
import ch.uzh.csg.reimbursement.model.exception.MaxExpenseItemsReachedException;
import ch.uzh.csg.reimbursement.model.exception.MaxFileSizeViolationException;
import ch.uzh.csg.reimbursement.model.exception.NoDateGivenException;
import ch.uzh.csg.reimbursement.model.exception.NotSupportedCurrencyException;
import ch.uzh.csg.reimbursement.model.exception.NotSupportedFileTypeException;
import ch.uzh.csg.reimbursement.model.exception.TokenNotFoundException;
import ch.uzh.csg.reimbursement.model.exception.ValidationException;
import ch.uzh.csg.reimbursement.repository.ExpenseItemRepositoryProvider;

@Service
@Transactional
public class ExpenseItemService {

	private final Logger LOG = LoggerFactory.getLogger(ExpenseItemService.class);

	@Autowired
	private ExpenseItemRepositoryProvider expenseItemRepository;

	@Autowired
	private ExpenseService expenseService;

	@Autowired
	private UserService userService;

	@Autowired
	private ExchangeRateService exchangeRateService;

	@Autowired
	private TokenService tokenService;

	@Autowired
	private CostCategoryService costCategoryService;

	@Autowired
	private UserResourceAuthorizationService authorizationService;

	@Autowired
	private PdfGenerationService pdfGenerationService;

	@Autowired
	private ValidationService validationService;

	@Value("${reimbursement.filesize.maxUploadFileSize}")
	private int maxUploadFileSize;

	public ExpenseItem createExpenseItem(String uid, ExpenseItemDto dto) {
		Expense expense = expenseService.getByUid(uid);

		if (authorizationService.checkEditAuthorization(expense)) {
			if (!validationService.canAddExpenseItem(expense)) {
				LOG.info("You have reached the max. number of expense-items for an expense.");
				throw new MaxExpenseItemsReachedException();
			}

			CostCategory category = costCategoryService.getByUid(dto.getCostCategoryUid());
			Double calculatedAmount = 0.0;
			Double exchangeRate = 0.0;
			ExchangeRateDto exchangeRates = null;

			if (dto.getDate() == null) {
				LOG.debug("Date should not be null");
				throw new NoDateGivenException();
			} else {
				exchangeRates = exchangeRateService.getExchangeRateFrom(new SimpleDateFormat("yyyy-MM-dd").format(dto
						.getDate()));
			}

			if (dto.getCurrency().equals(exchangeRates.getBase())) {
				exchangeRate = 1.0;
			} else if (!exchangeRateService.getSupportedCurrencies().contains(dto.getCurrency())) {
				LOG.debug("Given currency is not supported");
				throw new NotSupportedCurrencyException();
			} else {
				exchangeRate = exchangeRates.getRates().get(dto.getCurrency());
			}

			calculatedAmount = calculateAmount(dto.getOriginalAmount(), exchangeRate);
			exchangeRate = Math.round(exchangeRate * 100.0) / 100.0;
			ExpenseItem expenseItem = new ExpenseItem(category, exchangeRate, calculatedAmount, expense, dto);
			expenseItemRepository.create(expenseItem);

			return expenseItem;

		} else {
			LOG.debug("The logged in user has no access to this expense");
			throw new AccessException();
		}
	}

	public void updateExpenseItem(String uid, ExpenseItemDto dto) {
		ExpenseItem expenseItem = getByUid(uid);
		String keyExplanation = "expense.explanation";
		String keyProject = "expense.project";
		String keyAmount = "expense.amount";

		if (validationService.matches(keyExplanation, dto.getExplanation())
				&& validationService.checkProjectField(keyProject, dto.getProject())
				&& validationService.matches(keyAmount, Double.toString(dto.getOriginalAmount()))) {
			if (authorizationService.checkEditAuthorization(expenseItem)) {

				CostCategory category = costCategoryService.getByUid(dto.getCostCategoryUid());
				Double calculatedAmount = 0.0;
				Double exchangeRate = 0.0;

				ExchangeRateDto exchangeRates = exchangeRateService.getExchangeRateFrom(new SimpleDateFormat(
						"yyyy-MM-dd").format(dto.getDate()));

				if (dto.getCurrency().equals(exchangeRates.getBase())) {
					exchangeRate = 1.0;
				} else {
					exchangeRate = exchangeRates.getRates().get(dto.getCurrency());
				}
				calculatedAmount = calculateAmount(dto.getOriginalAmount(), exchangeRate);
				exchangeRate = Math.round(exchangeRate * 100.0) / 100.0;
				expenseItem.updateExpenseItem(category, exchangeRate, calculatedAmount, dto);
			} else {
				LOG.debug("The logged in user has no access to this expense");
				throw new AccessException();
			}
		} else {
			throw new ValidationException(keyExplanation + " | " + keyProject + " | " + keyAmount);
		}
	}

	private double calculateAmount(double originalAmount, double exchangeRate) {
		double tmp = originalAmount / exchangeRate;
		return Math.round(tmp * 100.0) / 100.0;
	}

	public ExpenseItem getByUid(String uid) {

		if (userService.userIsLoggedIn()) {
			return getByExpenseUid(uid);
		} else {
			return getByTokenUid(uid);
		}
	}

	private ExpenseItem getByExpenseUid(String uid) {
		ExpenseItem expenseItem = expenseItemRepository.findByUid(uid);

		if (expenseItem != null) {
			if (authorizationService.checkViewAuthorization(expenseItem)) {
				return expenseItem;
			} else {
				LOG.debug("The logged in user has no access to this expenseItem");
				throw new AccessException();
			}
		} else {
			LOG.debug("ExpenseItem not found in database with uid: " + uid);
			throw new ExpenseItemNotFoundException();
		}
	}

	private ExpenseItem getByTokenUid(String tokenUid) {
		Token token = tokenService.getByUid(tokenUid);
		ExpenseItem expenseItem;

		if (token != null) {
			expenseItem = expenseItemRepository.findByUid(token.getContent());
		} else {
			LOG.debug("The token has no access to this expenseItem");
			throw new TokenNotFoundException();
		}

		if (expenseItem != null) {
			if (authorizationService.checkViewAuthorizationMobile(expenseItem, token)) {
				return expenseItem;
			} else {
				LOG.debug("The token has no access to this expenseItem");
				throw new AccessException();
			}
		} else {
			LOG.info("ExpenseItem not found in database with uid: " + token.getContent());
			throw new ExpenseItemNotFoundException();
		}
	}

	public Set<ExpenseItem> getExpenseItemsByExpenseUid(String expenseUid) {
		Expense expense = expenseService.getByUid(expenseUid);
		return expense.getExpenseItems();
	}

	public Document setAttachment(String uid, MultipartFile multipartFile) {
		ExpenseItem expenseItem = getByUid(uid);
		Token token = tokenService.getByUid(uid);
		if (!(MIME_JPEG.equals(multipartFile.getContentType()) || MIME_PNG.equals(multipartFile.getContentType())
				|| MIME_GIF.equals(multipartFile.getContentType()) || MIME_PDF.equals(multipartFile.getContentType()))) {

			LOG.info("The uploaded file type is not supported.");
			throw new NotSupportedFileTypeException();
		} else if (multipartFile.getSize() >= maxUploadFileSize) {
			LOG.info("File too big, allowed: " + maxUploadFileSize + " actual: " + multipartFile.getSize());
			throw new MaxFileSizeViolationException();

		} else if (token != null) {
			if (authorizationService.checkEditAuthorizationMobile(expenseItem.getExpense(), token)) {
				return setAttachment(expenseItem,multipartFile);
			} else {
				LOG.debug("The token has no access to this expenseItem");
				throw new AccessException();
			}
		} else{
			if (authorizationService.checkEditAuthorization(expenseItem)) {
				return setAttachment(expenseItem,multipartFile);
			} else {
				LOG.debug("The logged in user has no access to this expenseItem");
				throw new AccessException();
			}
		}
	}

	private Document setAttachment(ExpenseItem expenseItem, MultipartFile multipartFile) {
		if (multipartFile.getContentType().equals(MIME_PDF)) {
			return expenseItem.setAttachment(multipartFile);

		} else {
			return expenseItem.setAttachment(pdfGenerationService.generateAttachmentPdf(multipartFile));
		}
	}

	public Document getAttachment(String expenseItemUid) {
		ExpenseItem expenseItem = getByUid(expenseItemUid);
		if (authorizationService.checkViewAuthorization(expenseItem)) {
			return expenseItem.getAttachment();
		} else {
			LOG.debug("The logged in user has no access to this expenseItem attachment");
			throw new AccessException();
		}
	}

	public void deleteAttachment(String expenseItemUid) {
		ExpenseItem expenseItem = getByUid(expenseItemUid);

		if (authorizationService.checkEditAuthorization(expenseItem)) {
			expenseItem.deleteAttachment();
		} else {
			LOG.debug("The logged in user has no access to this expenseItem attachment");
			throw new AccessException();
		}
	}

	public void deleteExpenseItem(String uid) {
		ExpenseItem expenseItem = getByUid(uid);

		if (authorizationService.checkEditAuthorization(expenseItem)) {
			expenseItemRepository.delete(getByUid(uid));
		} else {
			LOG.debug("The logged in user has no access to this expenseItem");
			throw new AccessException();
		}
	}

	// Returns a grouped/consolidated list based on expense-items with the same
	// cost-categories and project values.
	public Set<ExpenseItemPdfDto> getConsolidatedExpenseItems(String expenseUid) {
		Expense expense = expenseService.getByUid(expenseUid);
		Set<ExpenseItem> expenseItems = expense.getExpenseItems();

		Map<String, ExpenseItemPdfDto> consolidatedExpenseItems = new HashMap<String, ExpenseItemPdfDto>();

		for (ExpenseItem expenseItem : expenseItems) {
			int accountNumber = expenseItem.getCostCategory().getAccountNumber();
			String project = expenseItem.getProject();
			String key = accountNumber + project;

			if (consolidatedExpenseItems.get(key) == null) {
				ExpenseItemPdfDto dto = new ExpenseItemPdfDto(expenseItem.getCostCategory().getName().getDe(),
						expenseItem.getCostCategory().getAccountNumber(), expenseItem.getProject(),
						expenseItem.getCalculatedAmount());

				consolidatedExpenseItems.put(key, dto);
			} else {
				ExpenseItemPdfDto dto = consolidatedExpenseItems.get(key);
				dto.addAmount(expenseItem.getCalculatedAmount());
			}
		}

		return new HashSet<ExpenseItemPdfDto>(consolidatedExpenseItems.values());
	}
}
