package ch.uzh.csg.reimbursement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import ch.uzh.csg.reimbursement.model.Token;
import ch.uzh.csg.reimbursement.model.User;
import ch.uzh.csg.reimbursement.model.exception.TokenExpiredException;
import ch.uzh.csg.reimbursement.model.exception.TokenNotFoundException;
import ch.uzh.csg.reimbursement.repository.TokenRepositoryProvider;

@Service
@Transactional
public class MobileService {

	@Autowired
	private UserService userService;

	@Autowired
	private TokenRepositoryProvider repository;

	@Value("${reimbursement.token.signatureMobile.expirationInMilliseconds}")
	private int expirationInMilliseconds;

	public void createSignature(String tokenString, MultipartFile file) {
		Token token = repository.findByUid(tokenString);
		checkValidity(token);

		User user = token.getUser();

		repository.delete(token);

		userService.addSignature(user, file);
	}

	private void checkValidity(Token token) {
		if(token == null) {
			throw new TokenNotFoundException();
		}
		if(token.isExpired(expirationInMilliseconds)) {
			throw new TokenExpiredException();
		}
	}
}
