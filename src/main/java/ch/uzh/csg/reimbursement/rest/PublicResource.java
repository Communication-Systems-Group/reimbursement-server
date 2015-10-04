package ch.uzh.csg.reimbursement.rest;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import ch.uzh.csg.reimbursement.dto.ExchangeRateDto;
import ch.uzh.csg.reimbursement.dto.ExpenseUrlDto;
import ch.uzh.csg.reimbursement.model.Document;
import ch.uzh.csg.reimbursement.model.Expense;
import ch.uzh.csg.reimbursement.model.ExpenseItem;
import ch.uzh.csg.reimbursement.model.Language;
import ch.uzh.csg.reimbursement.service.ExchangeRateService;
import ch.uzh.csg.reimbursement.service.ExpenseItemService;
import ch.uzh.csg.reimbursement.service.ExpenseService;
import ch.uzh.csg.reimbursement.service.MobileService;
import ch.uzh.csg.reimbursement.service.PdfGenerationService;
import ch.uzh.csg.reimbursement.service.TokenService;
import ch.uzh.csg.reimbursement.service.UserService;
import ch.uzh.csg.reimbursement.view.View.DashboardSummary;

import com.fasterxml.jackson.annotation.JsonView;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/public")
@Api(value = "Public", description = "Unauthorized access.")
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
	private PdfGenerationService pdfGenerationService;

	@RequestMapping(value = "/mobile/{token}/signature", method = POST)
	@ApiOperation(value = "Create Signature from Mobile device")
	public void createSignature(@PathVariable("token") String token, @RequestParam("file") MultipartFile file) {

		mobileService.createSignature(token, file);
	}

	@RequestMapping(value = "/mobile/{token}/attachment", method = POST)
	@ApiOperation(value = "Create ExpenseItemAttachment from Mobile device")
	public void createExpenseItemAttachment(@PathVariable("token") String token,
			@RequestParam("file") MultipartFile file) {

		mobileService.createExpenseItemAttachment(token, file);
	}

	@JsonView(DashboardSummary.class)
	@RequestMapping(value = "/mobile/{token-uid}/expense", method = GET)
	@ApiOperation(value = "Get Expense from Mobile device")
	public Expense getExpenseForUniAdmin(@PathVariable("token-uid") String uid) {

		return mobileService.getExpenseByTokenUid(uid);
	}

	@RequestMapping(value = "/mobile/{token-uid}/expenses/expense-item", method = GET)
	@ApiOperation(value = "Get an expense-item with the given token from Mobile device")
	@ResponseStatus(CREATED)
	public ExpenseItem getExpenseItemByTokenUid(@PathVariable("token-uid") String uid) {

		return mobileService.getExpenseItemByTokenUid(uid);
	}

	@RequestMapping(value = "/mobile/{token-uid}/expenseItems", method = GET)
	@ApiOperation(value = "Get all expense-items of an expense from Mobile device")
	public Set<ExpenseItem> getAllExpenseItemsForUniAdmin(@PathVariable("token-uid") String uid) {

		return mobileService.getAllExpenseItemsByTokenUid(uid);
	}

	@RequestMapping(value = "/exchange-rate", method = GET)
	@ApiOperation(value = "Get the exchange rate from a date", notes = "The date needs to be in the format YYYY-MM-DD.")
	public ExchangeRateDto getExchangeRateFromDate(@RequestParam("date") String date) {

		return exchangeRateService.getExchangeRateFrom(date);
	}

	@RequestMapping(value = "/currencies", method = GET)
	@ApiOperation(value = "Gets a list of supported currencies")
	public List<String> getSupportedCurrencies() {

		return exchangeRateService.getSupportedCurrencies();
	}

	@RequestMapping(value = "/languages", method = GET)
	@ApiOperation(value = "Gets a list of supported languages")
	public List<Language> getSupportedLanguages() {

		return userService.getSupportedLanguages();
	}

	@RequestMapping(value = "/test", method = GET)
	@ApiOperation(value = "Gets a test")
	public Document getTest(@RequestParam("expenseUid") String expenseUid, @RequestParam("url") String url) {

		Expense expense = expenseService.findByUid(expenseUid);
		return pdfGenerationService.generatePdf(new ExpenseUrlDto(expense, url));

	}
}
