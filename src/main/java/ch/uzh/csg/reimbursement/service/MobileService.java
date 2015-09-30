package ch.uzh.csg.reimbursement.service;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import ch.uzh.csg.reimbursement.model.Document;
import ch.uzh.csg.reimbursement.model.Expense;
import ch.uzh.csg.reimbursement.model.ExpenseItem;
import ch.uzh.csg.reimbursement.model.Token;
import ch.uzh.csg.reimbursement.model.User;

@Service
@Transactional
public class MobileService {

	@Autowired
	private UserService userService;

	@Autowired
	private ExpenseItemService expenseItemService;

	@Autowired
	private ExpenseService expenseService;

	@Autowired
	private TokenService tokenService;

	public void createSignature(String tokenString, MultipartFile file) {
		Token token = tokenService.findByUid(tokenString);
		tokenService.checkValidity(token);
		User user = token.getUser();
		userService.addSignature(user, file);
		tokenService.delete(token);
	}

	public String createExpenseItemAttachment(String tokenString, MultipartFile file) {
		Token token = tokenService.findByUid(tokenString);
		tokenService.checkValidity(token);
		Document attachment = expenseItemService.setAttachmentMobile(token, file);
		tokenService.delete(token);
		// TODO Check if token is really deleted
		return attachment.getUid();
	}

	public Expense getExpenseByTokenUid(String uid) {
		Token token = tokenService.findByUid(uid);
		tokenService.checkValidity(token);
		Expense expense = expenseService.findByToken(token);
		tokenService.delete(token);
		return expense;
	}

	public Set<ExpenseItem> getAllExpenseItemsByTokenUid(String uid) {
		Expense expense = getExpenseByTokenUid(uid);
		return expense.getExpenseItems();
	}

	public ExpenseItem getExpenseItemByTokenUid(String uid) {
		Token token = tokenService.findByUid(uid);
		tokenService.checkValidity(token);
		ExpenseItem expenseItem = expenseItemService.findByToken(token);
		tokenService.delete(token);
		return expenseItem;
	}
}
