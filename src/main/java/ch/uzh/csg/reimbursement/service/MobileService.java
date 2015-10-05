package ch.uzh.csg.reimbursement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import ch.uzh.csg.reimbursement.model.Document;
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
}
