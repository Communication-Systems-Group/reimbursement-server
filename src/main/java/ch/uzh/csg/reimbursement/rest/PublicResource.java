package ch.uzh.csg.reimbursement.rest;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import ch.uzh.csg.reimbursement.application.validation.ValidationService;
import ch.uzh.csg.reimbursement.dto.ExchangeRateDto;
import ch.uzh.csg.reimbursement.model.CostCategory;
import ch.uzh.csg.reimbursement.model.Language;
import ch.uzh.csg.reimbursement.service.CostCategoryService;
import ch.uzh.csg.reimbursement.service.ExchangeRateService;
import ch.uzh.csg.reimbursement.service.ExpenseItemService;
import ch.uzh.csg.reimbursement.service.ExpenseService;
import ch.uzh.csg.reimbursement.service.MobileService;
import ch.uzh.csg.reimbursement.service.PdfGenerationService;
import ch.uzh.csg.reimbursement.service.TokenService;
import ch.uzh.csg.reimbursement.service.UserService;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/public")
@Api(value = "Public", description = "Unauthorized access")
public class PublicResource {

	@Autowired
	private MobileService mobileService;

	@Autowired
	private UserService userService;

	@Autowired
	private TokenService tokenService;

	@Autowired
	private ExpenseService expenseService;

	@Autowired
	private ExpenseItemService expenseItemService;

	@Autowired
	private ExchangeRateService exchangeRateService;

	@Autowired
	private CostCategoryService costCategoryService;

	@Autowired
	private PdfGenerationService pdfGenerationService;

	@Autowired
	private ValidationService validationService;

	@RequestMapping(value = "/mobile/{token}/signature", method = POST)
	@ApiOperation(value = "Create signature", notes = "Creates signature from mobile device.")
	@ResponseStatus(CREATED)
	public void createSignature(@PathVariable("token") String token, @RequestParam("file") MultipartFile file) {

		mobileService.createSignature(token, file);
	}

	@RequestMapping(value = "/mobile/{token}/attachment", method = POST)
	@ApiOperation(value = "Create attachment", notes = "Creates an attachment for an expense-item from mobile device.")
	@ResponseStatus(CREATED)
	public void createExpenseItemAttachment(@PathVariable("token") String token,
			@RequestParam("file") MultipartFile file) {

		mobileService.createExpenseItemAttachment(token, file);
	}

	@RequestMapping(value = "/exchange-rate", method = GET)
	@ApiOperation(value = "Get exchange rate", notes = "Returns the exchange rate from a date. The date needs to be in the format YYYY-MM-DD.")
	public ExchangeRateDto getExchangeRateFromDate(@RequestParam("date") String date) {

		return exchangeRateService.getExchangeRateFrom(date);
	}

	@RequestMapping(value = "/currencies", method = GET)
	@ApiOperation(value = "Get supported currencies", notes = "Returns a list of supported currencies.")
	public List<String> getSupportedCurrencies() {

		return exchangeRateService.getSupportedCurrencies();
	}

	@RequestMapping(value = "/languages", method = GET)
	@ApiOperation(value = "Get supported languages", notes = "Returns a list of supported languages.")
	public List<Language> getSupportedLanguages() {

		return userService.getSupportedLanguages();
	}

	@RequestMapping(value = "/cost-categories", method = GET)
	@ApiOperation(value = "Get active cost categories", notes = "Returns all active cost categories.")
	public List<CostCategory> getAllActiveCostCategories() {

		return costCategoryService.getAllActive();
	}

	@RequestMapping(value = "/validations", method = GET)
	@ApiOperation(value = "Get regular expressions", notes = "Provides all regular expressions for the front-end validation.")
	public Map<String, Pattern> getValidations() {

		return validationService.getRegularExpressions();
	}
}
