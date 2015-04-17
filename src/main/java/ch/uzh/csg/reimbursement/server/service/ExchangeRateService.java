package ch.uzh.csg.reimbursement.server.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import ch.uzh.csg.reimbursement.server.dto.ExchangeRateDto;

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
			throw new IllegalArgumentException(date);
		}

		return exchangeRateDto;
	}

	private String generateUrl(String date) {
		return providerUrl + date + "?base=" + base;
	}

}
