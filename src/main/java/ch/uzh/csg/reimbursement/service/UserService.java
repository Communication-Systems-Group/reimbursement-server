package ch.uzh.csg.reimbursement.service;

import static ch.uzh.csg.reimbursement.model.Role.PROF;
import static ch.uzh.csg.reimbursement.model.Role.REGISTERED_USER;
import static ch.uzh.csg.reimbursement.model.TokenType.SIGNATURE_MOBILE;
import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import ch.uzh.csg.reimbursement.application.ldap.LdapPerson;
import ch.uzh.csg.reimbursement.dto.CroppingDto;
import ch.uzh.csg.reimbursement.dto.SettingsDto;
import ch.uzh.csg.reimbursement.model.Language;
import ch.uzh.csg.reimbursement.model.Role;
import ch.uzh.csg.reimbursement.model.Signature;
import ch.uzh.csg.reimbursement.model.Token;
import ch.uzh.csg.reimbursement.model.User;
import ch.uzh.csg.reimbursement.model.exception.UserNotFoundException;
import ch.uzh.csg.reimbursement.model.exception.UserNotLoggedInException;
import ch.uzh.csg.reimbursement.repository.UserRepositoryProvider;

@Service
@Transactional
public class UserService {

	private final Logger LOG = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private UserRepositoryProvider repository;

	@Autowired
	private TokenService tokenService;

	@Value("${reimbursement.token.signatureMobile.expirationInMilliseconds}")
	private int tokenExpirationInMilliseconds;

	public List<User> getAll() {
		return repository.findAll();
	}

	public User getByUid(String uid) {
		User user = repository.findByUid(uid);

		if (user == null) {
			LOG.debug("User not found in database with uid: " + uid);
			throw new UserNotFoundException();
		}
		return user;
	}

	public List<User> getAllByLastName(String lastName) {
		return repository.findAllByLastName(lastName);
	}

	public void addSignature(MultipartFile file) {
		User user = getLoggedInUser();
		addSignature(user, file);
	}

	public void addSignature(User user, MultipartFile file) {
		user.setSignature(file);
	}

	public Signature getSignature() {
		User user = getLoggedInUser();
		return user.getSignature();
	}

	public void addSignatureCropping(CroppingDto dto) {
		User user = getLoggedInUser();
		user.addSignatureCropping(dto.getWidth(), dto.getHeight(), dto.getTop(), dto.getLeft());

		addRoleRegisteredUser(user);
	}

	private void addRoleRegisteredUser(User user) {
		if (!user.getRoles().contains(REGISTERED_USER)) {
			user.addRoleRegisteredUser();

			// add role to security context to refresh current logged in user's roles
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			List<GrantedAuthority> authorities = new ArrayList<>(auth.getAuthorities());
			authorities.add(new SimpleGrantedAuthority("ROLE_" + REGISTERED_USER.name()));
			Authentication newAuth = new UsernamePasswordAuthenticationToken(auth.getPrincipal(), auth.getCredentials(), authorities);
			SecurityContextHolder.getContext().setAuthentication(newAuth);
		}
	}

	public void synchronize(List<LdapPerson> ldapPersons) {
		for (LdapPerson ldapPerson : ldapPersons) {
			User user = repository.findByUid(ldapPerson.getUid());

			if (user != null) {
				// this role is handled by our system
				if (user.getRoles().contains(REGISTERED_USER)) {
					ldapPerson.addRole(REGISTERED_USER);
				}

				user.setFirstName(ldapPerson.getFirstName());
				user.setLastName(ldapPerson.getLastName());
				user.setEmail(ldapPerson.getEmail());
				user.setManagerName(ldapPerson.getManager());
				user.setRoles(ldapPerson.getRoles());

			} else {
				user = new User(ldapPerson.getFirstName(), ldapPerson.getLastName(), ldapPerson.getUid(),
						ldapPerson.getEmail(), ldapPerson.getManager(), ldapPerson.getRoles());

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
			user = getByUid(uid);
		} else {
			throw new UserNotLoggedInException("The requesting user is not logged in.");
		}
		return user;
	}

	public boolean userIsLoggedIn() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if (principal instanceof UserDetails) {
			return true;
		} else {
			return false;
		}
	}

	public Token createSignatureMobileToken() {
		User user = getLoggedInUser();
		Token token;
		Token previousToken = tokenService.getByTypeAndUser(SIGNATURE_MOBILE, user);

		if (previousToken != null) {
			if (previousToken.isExpired(tokenExpirationInMilliseconds)) {
				// generate new token uid only if it is expired
				previousToken.generateNewUid();
			}
			previousToken.setCreatedToNow();

			token = previousToken;
		} else {
			token = new Token(SIGNATURE_MOBILE, user);
			tokenService.createToken(token);
		}

		return token;
	}

	public List<User> getUsersByRole(Role role) {
		List<User> users = getAll();
		List<User> roleList = new ArrayList<User>();
		for (User user : users) {
			if (user.getRoles().contains(role)) {
				roleList.add(user);
			}
		}
		return roleList;
	}

	public List<User> getManagersWithoutMe() {
		List<User> managers = getUsersByRole(PROF);
		managers.remove(getLoggedInUser());
		return managers;
	}

	public void updateSettings(SettingsDto dto) {
		User user = getLoggedInUser();
		user.setLanguage(dto.getLanguage());
		if (dto.getPersonnelNumber() != null) {
			user.setPersonnelNumber(dto.getPersonnelNumber());
		}
		if (dto.getPhoneNumber() != null) {
			user.setPhoneNumber(dto.getPhoneNumber());
		}
		user.setIsActive(dto.getIsActive());
	}

	public List<Language> getSupportedLanguages() {
		return asList(Language.class.getEnumConstants());
	}

	public Role[] getRoles() {
		return Role.values();
	}

}
