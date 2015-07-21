package ch.uzh.csg.reimbursement.service;

import static ch.uzh.csg.reimbursement.model.TokenType.ATTACHMENT_MOBILE;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
	private TokenRepositoryProvider tokenRepository;

	@Autowired
	private CostCategoryService costCategoryService;

	@Value("${reimbursement.token.epxenseItemAttachmentMobile.expirationInMilliseconds}")
	private int tokenExpirationInMilliseconds;

	public ExpenseItem create(String uid, ExpenseItemDto dto) {
		Expense expense = expenseService.findByUid(uid);
		CostCategory category = costCategoryService.findByUid(dto.getCostCategoryUid());
		ExpenseItem expenseItem = new ExpenseItem(dto.getDate(), category, dto.getReason(), dto.getCurrency(),
				dto.getExchangeRate(), dto.getOriginalAmount(), dto.getCalculatedAmount(), dto.getCostCenter(), expense);
		expenseItemRepository.create(expenseItem);
		return expenseItem;
	}

	public void updateExpenseItem(String uid, ExpenseItemDto dto) {
		ExpenseItem expenseItem = expenseItemRepository.findByUid(uid);
		CostCategory category = costCategoryService.findByUid(dto.getCostCategoryUid());
		expenseItem.updateExpenseItem(dto.getDate(), category, dto.getReason(), dto.getCurrency(),
				dto.getExchangeRate(), dto.getOriginalAmount(), dto.getCalculatedAmount(), dto.getCostCenter());
	}

	public ExpenseItem findByUid(String uid) {
		ExpenseItem expenseItem = expenseItemRepository.findByUid(uid);

		if (expenseItem == null) {
			LOG.debug("ExpenseItem not found in database with uid: " + uid);
			throw new ExpenseItemNotFoundException();
		}
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

	public byte[] getExpenseItemAttachment(String expenseItemUid) {
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
