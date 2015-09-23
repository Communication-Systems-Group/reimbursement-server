package ch.uzh.csg.reimbursement.service;

import static ch.uzh.csg.reimbursement.model.ExpenseState.ASSIGNED_TO_FINANCE_ADMIN;
import static ch.uzh.csg.reimbursement.model.ExpenseState.ASSIGNED_TO_PROF;
import static ch.uzh.csg.reimbursement.model.ExpenseState.DRAFT;
import static ch.uzh.csg.reimbursement.model.ExpenseState.REJECTED;
import static ch.uzh.csg.reimbursement.model.ExpenseState.TO_BE_ASSIGNED;
import static ch.uzh.csg.reimbursement.model.ExpenseState.TO_SIGN_BY_USER;
import static ch.uzh.csg.reimbursement.model.Role.FINANCE_ADMIN;
import static ch.uzh.csg.reimbursement.model.Role.PROF;
import static ch.uzh.csg.reimbursement.model.Role.USER;
import static ch.uzh.csg.reimbursement.model.TokenType.GUEST_MOBILE;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.uzh.csg.reimbursement.dto.AccessRights;
import ch.uzh.csg.reimbursement.dto.CommentDto;
import ch.uzh.csg.reimbursement.dto.CreateExpenseDto;
import ch.uzh.csg.reimbursement.dto.ExpenseDto;
import ch.uzh.csg.reimbursement.dto.SearchExpenseDto;
import ch.uzh.csg.reimbursement.model.Expense;
import ch.uzh.csg.reimbursement.model.ExpenseState;
import ch.uzh.csg.reimbursement.model.Role;
import ch.uzh.csg.reimbursement.model.Token;
import ch.uzh.csg.reimbursement.model.User;
import ch.uzh.csg.reimbursement.model.exception.AccessViolationException;
import ch.uzh.csg.reimbursement.model.exception.AssignViolationException;
import ch.uzh.csg.reimbursement.model.exception.ExpenseDeleteViolationException;
import ch.uzh.csg.reimbursement.model.exception.ExpenseNotFoundException;
import ch.uzh.csg.reimbursement.repository.ExpenseRepositoryProvider;

@Service
@Transactional
public class ExpenseService {

	private final Logger LOG = LoggerFactory.getLogger(ExpenseService.class);

	@Autowired
	private ExpenseRepositoryProvider expenseRepository;

	@Autowired
	private UserResourceAuthorizationService authorizationService;

	@Autowired
	private UserService userService;

	@Autowired
	private TokenService tokenService;

	@Autowired
	private CostCategoryService costCategoryService;

	@Value("${reimbursement.token.epxenseItemAttachmentMobile.expirationInMilliseconds}")
	private int tokenExpirationInMilliseconds;

	public Expense create(CreateExpenseDto dto) {
		User user = userService.getLoggedInUser();

		Expense expense = new Expense(user, new Date(), null, dto.getAccounting(), DRAFT);
		expenseRepository.create(expense);

		return expense;
	}

	public Set<Expense> findAllByUser(String uid) {
		return expenseRepository.findAllByUser(uid);
	}

	public Set<Expense> findAllByAssignedManager(User user) {
		return expenseRepository.findAllByAssignedManager(user.getUid());
	}

	public Set<Expense> findAllForFinanceAdmin(String uid) {
		Set<Expense> expenses;
		expenses = expenseRepository.findAllByState(TO_BE_ASSIGNED);
		expenses.addAll(expenseRepository.findAllByFinanceAdmin(uid));

		return expenses;
	}

	public Set<Expense> findAllByState(ExpenseState state) {
		return expenseRepository.findAllByState(state);
	}

	public Set<Expense> getAllReviewExpenses() {
		User user = userService.getLoggedInUser();

		if (user.getRoles().contains(PROF)) {
			return findAllByAssignedManager(user);
		} else if (user.getRoles().contains(FINANCE_ADMIN)) {
			return findAllForFinanceAdmin(user.getUid());
		} else {
			LOG.debug("The logged in user has no access to this expense");
			throw new AccessViolationException();
		}
	}

	public Set<Expense> findAllByCurrentUser() {
		User user = userService.getLoggedInUser();
		return findAllByUser(user.getUid());
	}

	public void updateExpense(String uid, ExpenseDto dto) {
		Expense expense = findByUid(uid);

		if (authorizationService.checkEditAuthorization(expense)) {
			expense.setAccounting(dto.getAccounting());
		} else {
			LOG.debug("The logged in user has no access to this expense");
			throw new AccessViolationException();
		}
	}

	public Expense findByUid(String uid) {
		Expense expense = expenseRepository.findByUid(uid);

		if (expense == null) {
			LOG.debug("Expense not found in database with uid: " + uid);
			throw new ExpenseNotFoundException();
		} else if (authorizationService.checkViewAuthorization(expense)) {
			return expense;
		} else {
			LOG.debug("The logged in user has no access to this expense");
			throw new AccessViolationException();
		}
	}

	public Expense findByToken(Token token) {
		Expense expense = expenseRepository.findByUid(token.getContent());

		if (expense == null) {
			LOG.debug("Expense not found in database with uid: " + token.getContent());
			throw new ExpenseNotFoundException();
		} else if (authorizationService.checkViewAuthorizationMobile(expense, token)) {
			return expense;
		} else {
			LOG.debug("The logged in user has no access to this expense");
			throw new AccessViolationException();
		}
	}

	public void delete(String uid) {
		Expense expense = findByUid(uid);

		if (expense.getState() == DRAFT || expense.getState() == REJECTED) {
			expenseRepository.delete(expense);
		} else {
			LOG.debug("Expense cannot be deleted in this state");
			throw new ExpenseDeleteViolationException();
		}
	}

	public void acceptExpense(String uid) {
		Expense expense = findByUid(uid);

		if (expense.getState().equals(ASSIGNED_TO_PROF)) {
			assignExpenseToFinanceAdmin(expense);
		} else {
			expense.setState(TO_SIGN_BY_USER);
		}
	}

	public void assignExpenseToMe(String uid) {
		Expense expense = findByUid(uid);
		User user = userService.getLoggedInUser();

		if (authorizationService.checkEditAuthorization(expense)) {
			expense.setFinanceAdmin(user);
			expense.setState(ASSIGNED_TO_FINANCE_ADMIN);
		} else {
			LOG.debug("The logged in user has no access to this expense");
			throw new AccessViolationException();
		}
	}

	public void assignExpenseToProf(String uid) {
		Expense expense = findByUid(uid);
		User user = userService.getLoggedInUser();
		User financeAdmin = userService.findByUid("fadmin");

		if (authorizationService.checkEditAuthorization(expense)) {
			if (authorizationService.checkAssignAuthorization(expense)) {
				if (user.getRoles().contains(Role.PROF)) {
					// If the prof wants to hand in an expense the expense is
					// directly assigned to the chief of finance_admins
					assignExpenseToFinanceAdmin(expense, financeAdmin);
					// TODO The department manager should be set in the
					// application
					// properties
					User manager = userService.findByUid("lauber");
					expense.setAssignedManager(manager);
				} else {
					expense.setAssignedManager(user.getManager());
					expense.setState(ASSIGNED_TO_PROF);
				}
			} else {
				LOG.debug("Expenses without expenseItems cannot be assigned.");
				throw new AssignViolationException();
			}
		} else {
			LOG.debug("The logged in user has no access to this expense");
			throw new AccessViolationException();
		}
	}

	private void assignExpenseToFinanceAdmin(Expense expense, User financeAdmin) {
		expense.setFinanceAdmin(financeAdmin);
		assignExpenseToFinanceAdmin(expense);
	}

	public void assignExpenseToFinanceAdmin(Expense expense) {
		if (authorizationService.checkEditAuthorization(expense)) {
			if (authorizationService.checkAssignAuthorization(expense)) {
				expense.setState(TO_BE_ASSIGNED);
			} else {
				LOG.debug("Expenses without expenseItems cannot be assigned.");
				throw new AssignViolationException();
			}
		} else {
			LOG.debug("The logged in user has no access to this expense");
			throw new AccessViolationException();
		}
	}

	public void rejectExpense(String uid, CommentDto dto) {
		Expense expense = findByUid(uid);
		if (authorizationService.checkEditAuthorization(expense)) {
			expense.setState(REJECTED);
			expense.setRejectComment(dto.getText());
		} else {
			LOG.debug("The logged in user has no access to this expense");
			throw new AccessViolationException();
		}
	}

	public AccessRights getAccessRights(String uid) {
		AccessRights rights = new AccessRights();
		Expense expense;

		try {
			expense = findByUid(uid);
			rights.setViewable(true);

			if (authorizationService.checkEditAuthorization(expense)) {
				rights.setEditable(true);
			} else {
				rights.setEditable(false);
			}

			if (authorizationService.checkSignAuthorization(expense)) {
				rights.setSignable(true);
			} else {
				rights.setSignable(false);
			}
		} catch (AccessViolationException e) {
			rights.setViewable(false);
			rights.setEditable(false);
			rights.setSignable(false);
		}
		return rights;
	}

	public Token createUniAdminToken(String uid) {

		User user = userService.findByUid("guest");
		Token token;
		Token previousToken = tokenService.findByTypeAndUser(GUEST_MOBILE, user);

		if (previousToken != null) {
			if (previousToken.isExpired(tokenExpirationInMilliseconds)) {
				// generate new token uid only if it is expired
				previousToken.generateNewUid();
			}
			previousToken.setCreatedToNow();
			previousToken.setContent(uid);
			token = previousToken;
		} else {
			token = new Token(GUEST_MOBILE, user, uid);
			tokenService.create(token);
		}
		return token;
	}

	public Set<Expense> search(SearchExpenseDto dto) {
		String accountingText = "%";
		if(dto.getAccountingText() != null && !dto.getAccountingText().equals("")) {
			accountingText = "%"+dto.getAccountingText()+"%";
		}

		List<User> relevantUsers = new ArrayList<>();

		// search for the last name
		List<User> temporaryUsers;
		if(dto.getLastName() != null && !dto.getLastName().equals("")) {
			temporaryUsers = userService.findAllByLastName("%"+dto.getLastName()+"%");
		}
		else {
			temporaryUsers = userService.findAll();
		}

		// filter for the role
		if(dto.getRole() != null && !dto.getRole().equals("")) {
			Role role = null;
			try {
				role = Role.valueOf(dto.getRole());
			}
			catch(IllegalArgumentException e) {
				LOG.debug("Illegal role name, ignoring.");
			}
			if(role != null) {
				for(User user : temporaryUsers) {
					Set<Role> roles = user.getRoles();
					if(role == USER) {
						// if role is user, only the users and not admin/fadmin etc are added
						if(roles.contains(role) && roles.size() == 1) {
							relevantUsers.add(user);
						}
					}
					else {
						if(roles.contains(role)) {
							relevantUsers.add(user);
						}
					}
				}
			}
		}
		else {
			relevantUsers = temporaryUsers;
		}

		return expenseRepository.search(relevantUsers, accountingText);
	}
}
