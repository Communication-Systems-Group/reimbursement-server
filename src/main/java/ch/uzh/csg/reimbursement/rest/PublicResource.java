package ch.uzh.csg.reimbursement.rest;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import ch.uzh.csg.reimbursement.dto.ExchangeRateDto;
import ch.uzh.csg.reimbursement.service.ExchangeRateService;
import ch.uzh.csg.reimbursement.service.MobileService;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/public")
@Api(value = "Public", description = "Unauthorized access allowed.")
public class PublicResource {

	@Autowired
	private MobileService mobileService;

	@Autowired
	private ExchangeRateService exchangeRateService;

	@RequestMapping(value = "/mobile/{token}/signature", method = POST)
	@ApiOperation(value = "Create Signature from Mobile device")
	public void createSignature(@PathVariable("token") String token, @RequestParam("file") MultipartFile file) {

		mobileService.createSignature(token, file);
	}

	@RequestMapping(value = "/mobile/{token}/attachment", method = POST)
	@ApiOperation(value = "Create ExpenseItemAttachment from Mobile device")
	public void createExpenseItemAttachment(@PathVariable("token") String token, @RequestParam("file") MultipartFile file) {

		mobileService.createExpenseItemAttachment(token, file);
	}

	@RequestMapping(value ="/exchange-rate", method = GET)
	@ApiOperation(value = "Get the exchange rate from a date", notes = "The date needs to be in the format YYYY-MM-DD.")
	public ExchangeRateDto getExchangeRateFromDate(@RequestParam("date") String date) {

		return exchangeRateService.getExchangeRateFrom(date);
	}
}