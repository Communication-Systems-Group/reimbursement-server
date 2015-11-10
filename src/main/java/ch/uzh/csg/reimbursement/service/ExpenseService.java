package ch.uzh.csg.reimbursement.service;

import static ch.uzh.csg.reimbursement.model.ExpenseState.ASSIGNED_TO_FINANCE_ADMIN;
import static ch.uzh.csg.reimbursement.model.ExpenseState.ASSIGNED_TO_MANAGER;
import static ch.uzh.csg.reimbursement.model.ExpenseState.DRAFT;
import static ch.uzh.csg.reimbursement.model.ExpenseState.PRINTED;
import static ch.uzh.csg.reimbursement.model.ExpenseState.REJECTED;
import static ch.uzh.csg.reimbursement.model.ExpenseState.SIGNED;
import static ch.uzh.csg.reimbursement.model.ExpenseState.TO_BE_ASSIGNED;
import static ch.uzh.csg.reimbursement.model.ExpenseState.TO_SIGN_BY_FINANCE_ADMIN;
import static ch.uzh.csg.reimbursement.model.ExpenseState.TO_SIGN_BY_MANAGER;
import static ch.uzh.csg.reimbursement.model.ExpenseState.TO_SIGN_BY_USER;
import static ch.uzh.csg.reimbursement.model.Role.DEPARTMENT_MANAGER;
import static ch.uzh.csg.reimbursement.model.Role.HEAD_OF_INSTITUTE;
import static ch.uzh.csg.reimbursement.model.Role.PROF;
import static ch.uzh.csg.reimbursement.model.Role.USER;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.SECOND;
import static org.apache.xmlgraphics.util.MimeConstants.MIME_PDF;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import ch.uzh.csg.reimbursement.dto.ExpenseStateStatisticsDto;
import ch.uzh.csg.reimbursement.dto.SearchExpenseDto;
import ch.uzh.csg.reimbursement.model.CostCategory;
import ch.uzh.csg.reimbursement.model.Document;
import ch.uzh.csg.reimbursement.model.Expense;
import ch.uzh.csg.reimbursement.model.ExpenseState;
import ch.uzh.csg.reimbursement.model.Role;
import ch.uzh.csg.reimbursement.model.Token;
import ch.uzh.csg.reimbursement.model.User;
import ch.uzh.csg.reimbursement.model.exception.AccessException;
import ch.uzh.csg.reimbursement.model.exception.AssignException;
import ch.uzh.csg.reimbursement.model.exception.ExpenseDeleteViolationException;
import ch.uzh.csg.reimbursement.model.exception.ExpenseNotFoundException;
import ch.uzh.csg.reimbursement.model.exception.MaxFileSizeViolationException;
import ch.uzh.csg.reimbursement.model.exception.NotSupportedFileTypeException;
import ch.uzh.csg.reimbursement.model.exception.PdfExportException;
import ch.uzh.csg.reimbursement.model.exception.PdfGenerationException;
import ch.uzh.csg.reimbursement.model.exception.PdfSignException;
import ch.uzh.csg.reimbursement.model.exception.TokenNotFoundException;
import ch.uzh.csg.reimbursement.repository.ExpenseRepositoryProvider;

@Service
@Transactional
public class ExpenseService {

	private static final Logger LOG = LoggerFactory.getLogger(ExpenseService.class);

	@Autowired
	private ExpenseRepositoryProvider expenseRepository;

	@Autowired
	private UserResourceAuthorizationService authorizationService;

	@Autowired
	private UserService userService;

	@Autowired
	private TokenService tokenService;

	@Autowired
	private EmailService emailService;

	@Autowired
	CostCategoryService costCategoryService;

	@Autowired
	private PdfGenerationService pdfGenerationService;

	@Value("${reimbursement.token.expenseItemAttachmentMobile.expirationInMilliseconds}")
	private int tokenExpirationInMilliseconds;

	@Value("${reimbursement.filesize.maxUploadFileSize}")
	private int maxUploadFileSize;

	public Expense createExpense(String accounting) {
		User user = userService.getLoggedInUser();
		Expense expense = new Expense(user, null, accounting);
		expenseRepository.create(expense);

		return expense;
	}

	public Set<Expense> getAllByUser(String uid) {
		return expenseRepository.findAllByUser(uid);
	}

	public Set<Expense> getAllReviewExpenses() {
		User user = userService.getLoggedInUser();

		if (user.getRoles().contains(PROF) || user.getRoles().contains(DEPARTMENT_MANAGER) || user.getRoles().contains(HEAD_OF_INSTITUTE)) {
			return getAllByAssignedManager(user);
		} else {
			return getAllForFinanceAdmin(user);
		}
	}

	public Set<Expense> getAllByAssignedManager(User user) {
		return expenseRepository.findAllByAssignedManager(user);
	}

	public Set<Expense> getAllForFinanceAdmin(User user) {
		Set<Expense> expenses;
		// Get the review expenses for the finance admin
		// For finance admin all expenses have to be shown that are in the state
		// TO_BE_ASSIGNED
		expenses = expenseRepository.findAllByStateWithoutUser(TO_BE_ASSIGNED, user);
		// In addition to that the expenses that are assigned to the finance
		// admin have to be shown
		expenses.addAll(expenseRepository.findAllByFinanceAdmin(user));

		return expenses;
	}

	public Set<Expense> getAllByCurrentUser() {
		User user = userService.getLoggedInUser();
		return getAllByUser(user.getUid());
	}

	public void updateExpense(String uid, String accounting) {
		Expense expense = getByUid(uid);

		if (authorizationService.checkEditAuthorization(expense)) {
			expense.setAccounting(accounting);
		} else {
			LOG.debug("The logged in user has no access to this expense");
			throw new AccessException();
		}
	}

	public Expense getByUid(String uid) {

		if (userService.userIsLoggedIn()) {
			return getByExpenseUid(uid);
		} else {
			return getByTokenUid(uid);
		}
	}

	private Expense getByExpenseUid(String uid) {
		Expense expense = expenseRepository.findByUid(uid);

		if (expense != null) {
			if (authorizationService.checkViewAuthorization(expense)) {
				return expense;
			} else {
				LOG.debug("The logged in user has no access to this expense");
				throw new AccessException();
			}
		} else {
			LOG.debug("Expense not found in database with uid: " + uid);
			throw new ExpenseNotFoundException();
		}
	}

	private Expense getByTokenUid(String tokenUid) {
		Token token = tokenService.getByUid(tokenUid);
		Expense expense;

		if (token != null) {
			expense = expenseRepository.findByUid(token.getContent());
		} else {
			LOG.debug("The token for this expense could not be found");
			throw new TokenNotFoundException();
		}

		if (expense != null) {
			if (authorizationService.checkViewAuthorizationMobile(expense, token)) {
				return expense;
			} else {
				LOG.debug("The token has no access to this expense");
				throw new AccessException();
			}
		} else {
			LOG.debug("Expense not found in database with uid: " + tokenUid);
			throw new ExpenseNotFoundException();
		}
	}

	public void deleteExpense(String uid) {
		Expense expense = getByUid(uid);

		if (expense.getState() == DRAFT || expense.getState() == REJECTED) {
			expenseRepository.delete(expense);
		} else {
			LOG.debug("Expense cannot be deleted in this state");
			throw new ExpenseDeleteViolationException();
		}
	}

	//called by prof or finance admin
	public void acceptExpense(String uid) {
		Expense expense = getByUid(uid);

		if (authorizationService.checkEditAuthorization(expense)) {
			if (authorizationService.checkAssignAuthorization(expense)) {
				expense.goToNextState();
				emailService.sendEmailExpenseNewAssigned(expense.getCurrentEmailReceiverBasedOnExpenseState());

			} else {
				LOG.debug("Expenses without expenseItems cannot be assigned.");
				throw new AssignException();
			}
		} else {
			LOG.debug("The logged in user has no access to this expense");
			throw new AccessException();
		}
	}

	public void assignExpenseToMe(String uid) {
		Expense expense = getByUid(uid);
		User user = userService.getLoggedInUser();

		if (authorizationService.checkEditAuthorization(expense)) {
			expense.setFinanceAdmin(user);
			expense.goToNextState();
			emailService.sendEmailExpenseNewAssigned(expense.getFinanceAdmin());
		} else {
			LOG.debug("The logged in user has no access to this expense");
			throw new AccessException();
		}
	}

	public void assignExpenseToManager(String uid) {
		Expense expense = getByUid(uid);
		User user = userService.getLoggedInUser();

		if (authorizationService.checkEditAuthorization(expense)) {
			if (authorizationService.checkAssignAuthorization(expense)) {
				if (user.getManager().getIsActive()) {
					expense.setAssignedManager(user.getManager());
				} else {
					// If the user's manager is inactive the expense has to be assigned to the department manager who is the manager's manager
					expense.setAssignedManager(user.getManager().getManager());
				}
				expense.goToNextState();
				emailService.sendEmailExpenseNewAssigned(expense.getAssignedManager());
			} else {
				LOG.debug("Expenses without expenseItems cannot be assigned.");
				throw new AssignException();
			}
		} else {
			LOG.debug("The logged in user has no access to this expense");
			throw new AccessException();
		}
	}

	public void rejectExpense(String uid, String comment) {
		Expense expense = getByUid(uid);

		if (authorizationService.checkEditAuthorization(expense)) {
			expense.reject(comment);
			emailService.sendEmailExpenseNewAssigned(expense.getCurrentEmailReceiverBasedOnExpenseState());
		} else {
			LOG.debug("The logged in user has no access to this expense");
			throw new AccessException();
		}
	}

	public Set<Expense> search(SearchExpenseDto dto) {
		String accountingText = "%";
		Date startTime = null;
		Date endTime = null;
		CostCategory costCategory = null;

		if(dto.getCostCategoryUid() != null) {
			costCategory = costCategoryService.getByUid(dto.getCostCategoryUid());
		}

		if (dto.getAccountingText() != null && !dto.getAccountingText().equals("")) {
			accountingText = "%" + dto.getAccountingText() + "%";
		}

		if (dto.getStartTime() != null) {
			startTime = dto.getStartTime();
		}

		if (dto.getEndTime() != null) {
			endTime = dto.getEndTime();
		}

		ExpenseState state = null;
		if (dto.getExpenseState() != null && !dto.getExpenseState().equals("")) {
			try {
				state = ExpenseState.valueOf(dto.getExpenseState());
			} catch (IllegalArgumentException e) {
				LOG.debug("Illegal state name, ignoring.");
			}
		}

		List<User> relevantUsers = new ArrayList<>();

		// search for the last name
		List<User> temporaryUsers;

		if (dto.getLastName() != null && !dto.getLastName().equals("")) {
			temporaryUsers = userService.getAllByLastName("%" + dto.getLastName() + "%");
		} else {
			temporaryUsers = userService.getAll();
		}

		// filter for the role
		if (dto.getRole() != null && !dto.getRole().equals("")) {
			Role role = null;
			try {
				role = Role.valueOf(dto.getRole());
			} catch (IllegalArgumentException e) {
				LOG.debug("Illegal role name, ignoring.");
			}
			if (role != null) {
				for (User user : temporaryUsers) {
					Set<Role> roles = user.getRoles();
					if (role == USER) {
						// if role is user, only the users and not admin/fadmin
						// etc are added
						if (roles.contains(role) && roles.size() == 1) {
							relevantUsers.add(user);
						}
					} else {
						if (roles.contains(role)) {
							relevantUsers.add(user);
						}
					}
				}
			}
		} else {
			relevantUsers = temporaryUsers;
		}

		return expenseRepository.search(relevantUsers, accountingText, startTime, endTime, state, costCategory);
	}

	public Document setSignedPdf(String expenseUid, MultipartFile multipartFile) {
		Expense expense = getByUid(expenseUid);

		if (expense.getExpensePdf() == null) {
			LOG.info("PDF has not been generated yet");
			throw new PdfExportException();
		} else if (multipartFile.getSize() <= expense.getExpensePdf().getFileSize()) {
			LOG.info("File has not been changed");
			throw new PdfSignException();
		} else if (multipartFile.getSize() >= maxUploadFileSize) {
			LOG.info("File too big, allowed: " + maxUploadFileSize + " actual: " + multipartFile.getSize());
			throw new MaxFileSizeViolationException();
		} else if (!multipartFile.getContentType().equals(MIME_PDF)) {
			LOG.info("The uploaded file is not supported");
			throw new NotSupportedFileTypeException();
		} else {
			Document doc  = expense.setPdf(multipartFile);
			emailService.sendEmailPdfSet(expense.getCurrentEmailReceiverBasedOnExpenseState());
			return doc;
		}
	}

	public Document getPdf(String uid) {
		Expense expense = getByUid(uid);

		if (expense.getExpensePdf() == null) {
			LOG.debug("The PDF for the expense has not been generated yet");
			throw new PdfExportException();
		} else {
			return expense.getExpensePdf();
		}
	}

	public void generatePdf(String uid, String url) {
		Expense expense = getByUid(uid);
		if (authorizationService.checkPdfGenerationAuthorization(expense)) {
			String tokenUid = tokenService.createUniAdminToken(uid);
			String urlWithTokenUid = url + tokenUid;
			expense.setPdf(pdfGenerationService.generateExpensePdf(expense, urlWithTokenUid));
			emailService.sendEmailPdfSet(expense.getCurrentEmailReceiverBasedOnExpenseState());
		} else {
			LOG.debug("The PDF cannot be generated in this state");
			throw new PdfGenerationException();
		}
	}

	public ExpenseStateStatisticsDto getExpenseStateStatistics() {
		ExpenseStateStatisticsDto dto = new ExpenseStateStatisticsDto();

		dto.setTotalNumberOfExpenses(expenseRepository.countExpenses());
		dto.setDraft(expenseRepository.countByState(DRAFT));
		dto.setAssignedToManager(expenseRepository.countByState(ASSIGNED_TO_MANAGER));
		dto.setRejected(expenseRepository.countByState(REJECTED));
		dto.setToBeAssigned(expenseRepository.countByState(TO_BE_ASSIGNED));
		dto.setAssignedToFinanceAdmin(expenseRepository.countByState(ASSIGNED_TO_FINANCE_ADMIN));
		dto.setToSignByUser(expenseRepository.countByState(TO_SIGN_BY_USER));
		dto.setToSignByManager(expenseRepository.countByState(TO_SIGN_BY_MANAGER));
		dto.setToSignByFinanceAdmin(expenseRepository.countByState(TO_SIGN_BY_FINANCE_ADMIN));
		dto.setSigned(expenseRepository.countByState(SIGNED));
		dto.setPrinted(expenseRepository.countByState(PRINTED));
		if (dto.getTotalNumberOfExpenses() != 0) {
			dto.setPercentagePrinted((double) dto.getPrinted() / dto.getTotalNumberOfExpenses() * 100);
		}

		Date beginFirstQuarter = dateFromMonth(11);
		Date beginSecondQuarter = dateFromMonth(8);
		Date beginThirdQuarter = dateFromMonth(5);
		Date beginFourthQuarter = dateFromMonth(2);
		Date today = new Date();

		dto.setTotalAmountFirstQuarter(expenseRepository.sumTotalAmount(beginFirstQuarter, beginSecondQuarter));
		dto.setTotalAmountSecondQuarter(expenseRepository.sumTotalAmount(beginSecondQuarter, beginThirdQuarter));
		dto.setTotalAmountThirdQuarter(expenseRepository.sumTotalAmount(beginThirdQuarter, beginFourthQuarter));
		dto.setTotalAmountFourthQuarter(expenseRepository.sumTotalAmount(beginFourthQuarter, today));
		dto.setStartDate(beginFirstQuarter);

		return dto;
	}

	private Date dateFromMonth(int monthsBack) {

		Calendar cal = Calendar.getInstance();
		cal.add(MONTH, -monthsBack);
		cal.set(DAY_OF_MONTH, 1);
		cal.set(HOUR_OF_DAY, 0);
		cal.set(MINUTE, 0);
		cal.set(SECOND, 0);
		Date newDate = cal.getTime();

		return newDate;
	}

	public void setHasDigitalSignature(String uid, Boolean hasDigitalSignature) {
		Expense expense = getByUid(uid);
		if (authorizationService.checkDigitalSignatureDecision(expense)) {
			expense.setHasDigitalSignature(hasDigitalSignature);
		} else {
			LOG.debug("The logged in user has no access to this expense");
			throw new AccessException();
		}
	}

	public ExpenseState[] getExpenseStates() {
		return ExpenseState.values();
	}

	public Set<Expense> getArchive() {
		User user = userService.getLoggedInUser();
		return expenseRepository.findAllByStateForUser(PRINTED, user);
	}

	//can be called from everyone
	public void signElectronically(String uid) {
		Expense expense = getByUid(uid);
		if (authorizationService.checkSignAuthorization(expense)) {
			expense.goToNextState();
			emailService.sendEmailExpenseNewAssigned(expense.getCurrentEmailReceiverBasedOnExpenseState());
		} else {
			LOG.debug("The logged in user has no rights to sign this resource");
			throw new AccessException();
		}
	}
}
