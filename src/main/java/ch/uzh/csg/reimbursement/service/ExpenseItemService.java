package ch.uzh.csg.reimbursement.service;

import static ch.uzh.csg.reimbursement.model.TokenType.ATTACHMENT_MOBILE;

import java.text.SimpleDateFormat;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import ch.uzh.csg.reimbursement.dto.ExchangeRateDto;
import ch.uzh.csg.reimbursement.dto.ExpenseItemDto;
import ch.uzh.csg.reimbursement.model.CostCategory;
import ch.uzh.csg.reimbursement.model.Expense;
import ch.uzh.csg.reimbursement.model.ExpenseItem;
import ch.uzh.csg.reimbursement.model.ExpenseItemAttachment;
import ch.uzh.csg.reimbursement.model.Token;
import ch.uzh.csg.reimbursement.model.User;
import ch.uzh.csg.reimbursement.model.exception.ExpenseItemNotFoundException;
import ch.uzh.csg.reimbursement.repository.ExpenseItemRepositoryProvider;
import ch.uzh.csg.reimbursement.repository.TokenRepositoryProvider;

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
	private TokenRepositoryProvider tokenRepository;

	@Autowired
	private CostCategoryService costCategoryService;

	@Value("${reimbursement.token.epxenseItemAttachmentMobile.expirationInMilliseconds}")
	private int tokenExpirationInMilliseconds;

	public ExpenseItem create(String uid, ExpenseItemDto dto) {
		Expense expense = expenseService.findByUid(uid);
		CostCategory category = costCategoryService.findByUid(dto.getCostCategoryUid());

		Double calculatedAmount = 0.0;
		Double exchangeRate = 0.0;
		ExchangeRateDto exchangeRates = exchangeRateService.getExchangeRateFrom(new SimpleDateFormat("yyyy-MM-dd").format(dto.getDate()));

		if(dto.getCurrency().equals(exchangeRates.getBase())) {
			exchangeRate = 1.0;
		}
		else {
			exchangeRate = exchangeRates.getRates().get(dto.getCurrency());
		}
		calculatedAmount = exchangeRate*dto.getOriginalAmount();

		ExpenseItem expenseItem = new ExpenseItem(dto.getDate(), category, dto.getExplanation(), dto.getCurrency(),
				exchangeRate, dto.getOriginalAmount(), calculatedAmount, dto.getProject(), expense);
		expenseItemRepository.create(expenseItem);
		return expenseItem;
	}

	public void updateExpenseItem(String uid, ExpenseItemDto dto) {
		ExpenseItem expenseItem = findByUid(uid);
		CostCategory category = costCategoryService.findByUid(dto.getCostCategoryUid());
		Double calculatedAmount = 0.0;
		Double exchangeRate = 0.0;

		ExchangeRateDto exchangeRates = exchangeRateService.getExchangeRateFrom(new SimpleDateFormat("yyyy-MM-dd").format(dto.getDate()));

		if(dto.getCurrency().equals(exchangeRates.getBase())) {
			exchangeRate = 1.0;
		}
		else {
			exchangeRate = exchangeRates.getRates().get(dto.getCurrency());
		}
		calculatedAmount = exchangeRate*dto.getOriginalAmount();
		expenseItem.updateExpenseItem(dto.getDate(), category, dto.getExplanation(), dto.getCurrency(),
				exchangeRate, dto.getOriginalAmount(), calculatedAmount, dto.getProject());
	}

	public ExpenseItem findByUid(String uid) {
		ExpenseItem expenseItem = expenseItemRepository.findByUid(uid);

		if (expenseItem == null) {
			LOG.debug("ExpenseItem not found in database with uid: " + uid);
			throw new ExpenseItemNotFoundException();
		}
		//		TODO These security checks should be outsourced somewhere, or I have to come up with a solution for mobile tokens...
		//			else if ((expenseItem.getExpense().getState() == ExpenseState.DRAFT || expenseItem.getExpense().getState() == ExpenseState.REJECTED)
		//				&& expenseItem.getExpense().getUser() != userService.getLoggedInUser()) {
		//			LOG.debug("The logged in user has no access to this expenseItem");
		//			throw new ExpenseItemAccessViolationException();
		//		} else if ((expenseItem.getExpense().getState() != ExpenseState.DRAFT && expenseItem.getExpense().getState() != ExpenseState.REJECTED)
		//				&& expenseItem.getExpense().getAssignedManager() != userService.getLoggedInUser()) {
		//			LOG.debug("Expense not assigned to logged in user therefore logged in user has no access to this expenseItem");
		//			throw new ExpenseItemAccessViolationException();
		//		}
		return expenseItem;
	}

	public Set<ExpenseItem> findAllExpenseItemsByExpenseUid(String uid) {
		Expense expense = expenseService.findByUid(uid);
		return expense.getExpenseItems();
	}

	public ExpenseItemAttachment setAttachment(String expenseItemUid, MultipartFile multipartFile) {
		ExpenseItem expenseItem = findByUid(expenseItemUid);
		return expenseItem.setExpenseItemAttachment(multipartFile);
	}

	public ExpenseItemAttachment getExpenseItemAttachment(String expenseItemUid) {
		ExpenseItem expenseItem = findByUid(expenseItemUid);
		return expenseItem.getExpenseItemAttachment();
	}

	public Token createExpenseItemAttachmentMobileToken(String expenseItemUid) {
		User user = userService.getLoggedInUser();
		Token token;

		Token previousToken = tokenRepository.findByTypeAndUser(ATTACHMENT_MOBILE, user);
		if (previousToken != null) {
			if (previousToken.isExpired(tokenExpirationInMilliseconds)) {
				// generate new token uid only if it is expired
				previousToken.generateNewUid();
			}
			previousToken.setCreatedToNow();
			previousToken.setContent(expenseItemUid);
			token = previousToken;
		} else {
			token = new Token(ATTACHMENT_MOBILE, user);
			tokenRepository.create(token);
		}
		return token;
	}

	public void delete(String uid) {
		expenseItemRepository.delete(findByUid(uid));
	}
}