package ch.uzh.csg.reimbursement.service;

import static ch.uzh.csg.reimbursement.model.TokenType.SIGNATURE_MOBILE;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import ch.uzh.csg.reimbursement.application.ldap.LdapPerson;
import ch.uzh.csg.reimbursement.dto.CroppingDto;
import ch.uzh.csg.reimbursement.model.Token;
import ch.uzh.csg.reimbursement.model.User;
import ch.uzh.csg.reimbursement.model.exception.UserNotFoundException;
import ch.uzh.csg.reimbursement.model.exception.UserNotLoggedInException;
import ch.uzh.csg.reimbursement.repository.TokenRepositoryProvider;
import ch.uzh.csg.reimbursement.repository.UserRepositoryProvider;

@Service
@Transactional
public class UserService {

	private final Logger LOG = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private UserRepositoryProvider repository;

	@Autowired
	private TokenRepositoryProvider tokenRepository;

	@Value("${reimbursement.token.signatureMobile.expirationInMilliseconds}")
	private int tokenExpirationInMilliseconds;

	public List<User> findAll() {
		return repository.findAll();
	}

	public User findByUid(String uid) {
		User user = repository.findByUid(uid);

		if (user == null) {
			LOG.debug("User not found in database with uid: " + uid);
			throw new UserNotFoundException();
		}
		return user;
	}

	public void addSignature(MultipartFile file) {
		User user = getLoggedInUser();
		addSignature(user, file);
	}

	public void addSignature(User user, MultipartFile file) {
		user.setSignature(file);
	}

	public byte[] getSignature() {
		User user = getLoggedInUser();
		return user.getSignature();
	}

	public void addSignatureCropping(CroppingDto dto) {
		User user = getLoggedInUser();
		user.addSignatureCropping(dto.getWidth(), dto.getHeight(), dto.getTop(), dto.getLeft());
	}

	public void synchronize(List<LdapPerson> ldapPersons) {
		for (LdapPerson ldapPerson : ldapPersons) {
			User user = repository.findByUid(ldapPerson.getUid());

			if (user != null) {
				user.setFirstName(ldapPerson.getFirstName());
				user.setLastName(ldapPerson.getLastName());
				user.setEmail(ldapPerson.getEmail());
				user.setManagerName(ldapPerson.getManager());

			} else {
				user = new User(ldapPerson.getFirstName(), ldapPerson.getLastName(), ldapPerson.getUid(),
						ldapPerson.getEmail(), ldapPerson.getManager());

				repository.create(user);
			}
		}

		// Find the uid of the manager and save it
		List<User> users1 = repository.findAll();
		for (User user1 : users1) {
			List<User> users2 = repository.findAll();
			for (User user2 : users2) {
				if (user1.getManagerName() != null && user1.getManagerName().equals(user2.getUid())) {
					user1.setManager(user2);
				}
			}
			if (user1.getManager() == null) {
				LOG.warn("No Manager found for " + user1.getFirstName() + " " + user1.getLastName() + ".");
			}
		}
	}

	public User getLoggedInUser() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		User user;
		if (principal instanceof UserDetails) {
			String uid = ((UserDetails) principal).getUsername();
			user = findByUid(uid);
		} else {
			throw new UserNotLoggedInException("The requesting user is not logged in.");
		}
		return user;
	}

	public Token createSignatureMobileToken() {
		User user = getLoggedInUser();

		Token previousToken = tokenRepository.findByTypeAndUser(SIGNATURE_MOBILE, user);
		if(previousToken != null) {
			if(previousToken.isExpired(tokenExpirationInMilliseconds)) {
				tokenRepository.delete(previousToken);
			}
			else {
				// do not refresh the uid of the token, if it is still valid.
				// generating always a new token causes some trouble in the front-end
				previousToken.setCreatedToNow();
				return previousToken;
			}
		}

		Token token = new Token(SIGNATURE_MOBILE, user);
		tokenRepository.create(token);

		return token;
	}

}
