package ch.uzh.csg.reimbursement.application.validation;

import static ch.uzh.csg.reimbursement.model.Role.DEPARTMENT_MANAGER;
import static ch.uzh.csg.reimbursement.model.Role.FINANCE_ADMIN;
import static ch.uzh.csg.reimbursement.model.Role.PROF;
import static java.util.Collections.unmodifiableMap;
import static java.util.regex.Pattern.compile;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.csg.reimbursement.model.Expense;
import ch.uzh.csg.reimbursement.model.Role;
import ch.uzh.csg.reimbursement.model.exception.ValidationNotFoundException;
import ch.uzh.csg.reimbursement.service.UserService;

@Service
public class ValidationService {

	@Autowired
	private UserService userService;

	private static final Integer MAX_NUMBER_OF_EXPENSE_ITEMS_ALLOWED = 15;

	public Map<String, Pattern> getRegularExpressions() {
		@SuppressWarnings("serial")
		Map<String, Pattern> map = new HashMap<String, Pattern>() {
			{
				put("settings.personnelNumber", compile("^(?!0)\\d{7}$"));
				put("settings.phoneNumber", compile("^\\+{0,1}[0-9 \\-/\\\\]{10,}$"));
				put("expense.sapDescription", compile("^.{5,50}$"));
				put("expense.amount", compile("^(([1-9][0-9]*.\\d{0,2})|([0]*.[1-9][0-9]*)|([1-9]*))$"));
				put("expense.project", compile("^.{5,255}$"));
				put("expense.explanation", compile("^.{5,255}$"));
				put("expense.reject.reason", compile("^.{5,255}$"));
				put("expense.maxExpenseItems", compile(MAX_NUMBER_OF_EXPENSE_ITEMS_ALLOWED.toString()));
				put("expense.sign.privateKey", compile("^.{255,}$"));
				put("admin.search.lastname", compile("^.{5,50}$"));
				put("admin.search.sapDescription", compile("^.{5,50}$"));
				put("admin.costCategories.number", compile("^[0-9]+$"));
				put("admin.costCategories.name", compile("^.{5,50}$"));
				put("admin.costCategories.description", compile("^.{5,255}$"));
			}
		};

		return unmodifiableMap(map);
	}

	private Pattern getPattern(String key) {
		Map<String, Pattern> regularExpressions = getRegularExpressions();

		Pattern pattern = regularExpressions.get(key);
		if(pattern == null) {
			throw new ValidationNotFoundException();
		}
		return pattern;
	}

	public boolean matches(String key, String testingValue) {
		Pattern pattern = getPattern(key);

		Matcher matcher = pattern.matcher(testingValue);
		return matcher.find();
	}

	/*
	 * The field project is not required for users with no special roles, because these users normally don't know the project's name.
	 * In these cases the manager has to define the name of the project.
	 * When the user has a special role, he already knows the project and therefore the field is required in those cases.
	 */
	public boolean checkProjectField(String key, String testingValue) {
		Set<Role> userRoles = userService.getLoggedInUser().getRoles();

		if(userRoles.contains(PROF) || userRoles.contains(FINANCE_ADMIN) || userRoles.contains(DEPARTMENT_MANAGER)) {
			return matches(key, testingValue);
		} else {
			return true;
		}
	}

	public boolean canAddExpenseItem(Expense expense) {
		return (expense.getExpenseItems().size() < MAX_NUMBER_OF_EXPENSE_ITEMS_ALLOWED);
	}
}
