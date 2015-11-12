package ch.uzh.csg.reimbursement.application.validation;

import static java.util.Collections.unmodifiableMap;
import static java.util.regex.Pattern.compile;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import ch.uzh.csg.reimbursement.model.exception.ValidationNotFoundException;

@Service
public class ValidationService {

	public Map<String, Pattern> getRegularExpressions() {

		@SuppressWarnings("serial")
		Map<String, Pattern> map = new HashMap<String, Pattern>() {
			{
				put("settings.personnelNumber", compile("^[0-9\\-]{6,}$"));
				put("settings.phoneNumber", compile("^\\+{0,1}[0-9 \\-/\\\\]{10,}$"));
				put("expense.sapDescription", compile("(\\d|[\\S](?!.*\\s{2,}).*[\\S]|\\w){5,50}"));
				put("expense.amount", compile(""));
				put("expense.project", compile("(\\d|[\\S](?!.*\\s{2,}).*[\\S]|\\w){5,255}"));
				put("expense.explanation", compile("(\\d|[\\S](?!.*\\s{2,}).*[\\S]|\\n|\\w){5,50}"));
				put("expense.reject.reason", compile("(\\d|[\\S](?!.*\\s{2,}).*[\\S]|\\w){10,50}"));
				put("admin.search.lastname", compile("(\\d|[\\S](?!.*\\s{2,}).*[\\S]|\\w){3,50}"));
				put("admin.search.sapDescription", compile("(\\d|[\\S](?!.*\\s{2,}).*[\\S]|\\w){3,50}"));
				put("admin.costCategories.number", compile("^[0-9]+$"));
				put("admin.costCategories.name", compile("(\\d|[\\S](?!.*\\s{2,}).*[\\S]|\\w){2,50}"));
				put("admin.costCategories.description", compile("(\\d|[\\S](?!.*\\s{2,}).*[\\S]|\\n|\\w){3,50}"));
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
}
