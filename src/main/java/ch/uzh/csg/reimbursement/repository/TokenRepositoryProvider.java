package ch.uzh.csg.reimbursement.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.csg.reimbursement.model.Token;

@Service
public class TokenRepositoryProvider {

	@Autowired
	private TokenRepository tokenRepository;

	public void create(Token token) {
		tokenRepository.save(token);
	}

	public void delete(Token token) {
		tokenRepository.delete(token);
	}

	public Token findByUid(String uid) {
		return tokenRepository.findByUid(uid);
	}

}
