package ch.uzh.csg.reimbursement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import ch.uzh.csg.reimbursement.model.Token;
import ch.uzh.csg.reimbursement.model.User;
import ch.uzh.csg.reimbursement.model.exception.TokenNotFoundException;
import ch.uzh.csg.reimbursement.repository.TokenRepositoryProvider;

@Service
@Transactional
public class MobileService {

	@Autowired
	private UserService userService;

	@Autowired
	private TokenRepositoryProvider repository;

	@Value("${reimbursement.token.signatureMobile.expirationInDays}")
	private int expirationInDays;

	public void createSignature(String tokenString, MultipartFile file) {
		Token token = repository.findByUid(tokenString);
		checkValidity(token);
		// TODO remove previous tokens of that user and type

		User user = token.getUser();
		userService.addSignature(user, file);
	}

	private void checkValidity(Token token) {
		if(token == null) {
			throw new TokenNotFoundException();
		}
		// TODO check token expiration with expirationInDays
		/*if(token.getCreated().before(new Date())) {
			throw new TokenExpiredException();
		}*/
	}
}
