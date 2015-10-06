package ch.uzh.csg.reimbursement.rest;

import static org.springframework.util.ResourceUtils.getFile;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import ch.uzh.csg.reimbursement.application.barcode.QRCode;
import ch.uzh.csg.reimbursement.dto.ExchangeRateDto;
import ch.uzh.csg.reimbursement.dto.ExpenseUrlDto;
import ch.uzh.csg.reimbursement.model.Document;
import ch.uzh.csg.reimbursement.model.Expense;
import ch.uzh.csg.reimbursement.model.Language;
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
		BufferedImage image = null;
		
		QRCode q = new QRCode();
		image = q.generateImage(url);
		
		String file;
		try {
			file = getFile("classpath:/").getAbsolutePath();
			ImageIO.write(image, "png", new File(file + "/img/qrcode.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Expense expense = expenseService.findByUid(expenseUid);
		return pdfGenerationService.generatePdf(new ExpenseUrlDto(expense, url));
	}
}
