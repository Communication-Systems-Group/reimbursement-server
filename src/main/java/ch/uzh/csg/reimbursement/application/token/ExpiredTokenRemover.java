package ch.uzh.csg.reimbursement.application.token;

import static ch.uzh.csg.reimbursement.model.TokenType.SIGNATURE_MOBILE;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ch.uzh.csg.reimbursement.model.Token;
import ch.uzh.csg.reimbursement.repository.TokenRepositoryProvider;

@Component
public class ExpiredTokenRemover {

	private static final Logger LOG = LoggerFactory.getLogger(ExpiredTokenRemover.class);

	@Autowired
	private TokenRepositoryProvider repository;

	@Value("${reimbursement.token.signatureMobile.expirationInMilliseconds}")
	private int signatureMobileExpirationInMilliseconds;

	@Scheduled(fixedRateString = "${reimbursement.token.destroyExpiredTokens.intervalInMilliseconds}")
	public void removeExpiredTokens() {
		List<Token> tokens = repository.findAll();
		for(Token token : tokens) {
			if(token.getType() == SIGNATURE_MOBILE) {
				if(token.isExpired(signatureMobileExpirationInMilliseconds)) {
					repository.delete(token);
					LOG.info("Token "+token.getUid()+" by "+token.getUser().getUid()+" was automatically removed (expired).");
					token = null;
				}
			}
			// make sure to remove all other types with their expiration here
		}
	}

}