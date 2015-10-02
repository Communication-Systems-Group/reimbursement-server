package ch.uzh.csg.reimbursement.service;

import java.text.SimpleDateFormat;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import ch.uzh.csg.reimbursement.dto.ExchangeRateDto;
import ch.uzh.csg.reimbursement.dto.ExpenseItemDto;
import ch.uzh.csg.reimbursement.model.CostCategory;
import ch.uzh.csg.reimbursement.model.Document;
import ch.uzh.csg.reimbursement.model.Expense;
import ch.uzh.csg.reimbursement.model.ExpenseItem;
import ch.uzh.csg.reimbursement.model.Token;
import ch.uzh.csg.reimbursement.model.exception.AccessViolationException;
import ch.uzh.csg.reimbursement.model.exception.ExpenseItemNotFoundException;
import ch.uzh.csg.reimbursement.model.exception.NoDateGivenException;
import ch.uzh.csg.reimbursement.model.exception.NotSupportedCurrencyException;
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

	public ExpenseItem create(String uid, ExpenseItemDto dto) {
		Expense expense = expenseService.findByUid(uid);

		if (authorizationService.checkEditAuthorization(expense)) {
			CostCategory category = costCategoryService.findByUid(dto.getCostCategoryUid());
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
			ExpenseItem expenseItem = new ExpenseItem(category, exchangeRate, calculatedAmount, expense, dto);
			expenseItemRepository.create(expenseItem);

			return expenseItem;

		} else {
			LOG.debug("The logged in user has no access to this expense");
			throw new AccessViolationException();
		}
	}

	public void updateExpenseItem(String uid, ExpenseItemDto dto) {
		ExpenseItem expenseItem = findByUid(uid);

		if (authorizationService.checkEditAuthorization(expenseItem)) {
			CostCategory category = costCategoryService.findByUid(dto.getCostCategoryUid());
			Double calculatedAmount = 0.0;
			Double exchangeRate = 0.0;

			ExchangeRateDto exchangeRates = exchangeRateService.getExchangeRateFrom(new SimpleDateFormat("yyyy-MM-dd")
			.format(dto.getDate()));

			if (dto.getCurrency().equals(exchangeRates.getBase())) {
				exchangeRate = 1.0;
			} else {
				exchangeRate = exchangeRates.getRates().get(dto.getCurrency());
			}
			calculatedAmount = calculateAmount(dto.getOriginalAmount(), exchangeRate);
			expenseItem.updateExpenseItem(category, exchangeRate, calculatedAmount, dto);
		} else {
			LOG.debug("The logged in user has no access to this expense");
			throw new AccessViolationException();
		}
	}

	private double calculateAmount(double originalAmount, double exchangeRate) {
		return originalAmount / exchangeRate;

	}

	public ExpenseItem findByUid(String uid) {
		ExpenseItem expenseItem = expenseItemRepository.findByUid(uid);
		if (expenseItem == null) {
			LOG.debug("ExpenseItem not found in database with uid: " + uid);
			throw new ExpenseItemNotFoundException();
		} else if (authorizationService.checkViewAuthorization(expenseItem)) {
			return expenseItem;
		} else {
			LOG.debug("The logged in user has no access to this expense");
			throw new AccessViolationException();
		}
	}

	public ExpenseItem findByToken(Token token) {
		ExpenseItem expenseItem = expenseItemRepository.findByUid(token.getContent());
		if (expenseItem == null) {
			LOG.debug("ExpenseItem not found in database with uid: " + token.getContent());
			throw new ExpenseItemNotFoundException();
		} else if (authorizationService.checkViewAuthorizationMobile(expenseItem, token)) {
			return expenseItem;
		} else {
			LOG.debug("The token has no access to this expense");
			throw new AccessViolationException();
		}
	}

	public Set<ExpenseItem> getExpenseItemsByExpenseUid(String expenseUid) {
		Expense expense = expenseService.findByUid(expenseUid);
		return expense.getExpenseItems();
	}

	public Document setAttachment(String expenseItemUid, MultipartFile multipartFile) {
		ExpenseItem expenseItem = findByUid(expenseItemUid);
		return expenseItem.setAttachment(multipartFile);
	}

	public Document setAttachmentMobile(Token token, MultipartFile multipartFile) {
		ExpenseItem expenseItem = findByToken(token);
		return expenseItem.setAttachment(multipartFile);
	}

	public Document getAttachment(String expenseItemUid) {
		ExpenseItem expenseItem = findByUid(expenseItemUid);
		return expenseItem.getAttachment();
	}

	public void delete(String uid) {
		expenseItemRepository.delete(findByUid(uid));
	}
}