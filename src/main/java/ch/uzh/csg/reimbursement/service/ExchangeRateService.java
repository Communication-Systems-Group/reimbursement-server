package ch.uzh.csg.reimbursement.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import ch.uzh.csg.reimbursement.dto.ExchangeRateDto;
import ch.uzh.csg.reimbursement.model.exception.InvalidDateException;

@Service
public class ExchangeRateService {

	@Value("${reimbursement.exchangeRate.url}")
	private String providerUrl;

	@Value("${reimbursement.exchangeRate.base}")
	private String base;

	@Cacheable("exchange-rates")
	public ExchangeRateDto getExchangeRateFrom(String date) {
		String url = generateUrl(date);

		RestTemplate restTemplate = new RestTemplate();
		ExchangeRateDto exchangeRateDto = null;

		try {
			exchangeRateDto = restTemplate.getForObject(url, ExchangeRateDto.class);
		} catch (RestClientException e) {
		}

		/*
		 * If the date argument is not valid, the server returns an HTTP status
		 * error code. This error is wrapped here.
		 */
		if (exchangeRateDto == null) {
			throw new InvalidDateException(date);
		}

		return exchangeRateDto;
	}

	public Set<String> getSupportedCurrencies() {
		ExchangeRateDto dto = getExchangeRateFrom(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
		return dto.getRates().keySet();
	}

	private String generateUrl(String date) {
		return providerUrl + date + "?base=" + base;
	}

}
