package ch.uzh.csg.reimbursement.server.rest;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.uzh.csg.reimbursement.server.dto.ExchangeRateDto;
import ch.uzh.csg.reimbursement.server.service.ExchangeRateService;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/exchange-rate")
@Api(value = "Exchange Rate", description = "Retrieve Exchange Rate Information")
public class ExchangeRateResource {

	@Autowired
	private ExchangeRateService service;

	@RequestMapping(method = GET)
	@ApiOperation(value = "Get the exchange rate from a date", notes = "Gets the exchange rate from a specific date."
			+ "The date needs to be in the format YYYY-MM-DD")
	public ExchangeRateDto getExchangeRateFromDate(@RequestParam("date") String date) {

		return service.getExchangeRateFrom(date);
	}
}
