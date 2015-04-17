package ch.uzh.csg.reimbursement.server.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import ch.uzh.csg.reimbursement.server.model.ExchangeRate;

@Service
public class ExchangeRateService {

	@Value("${reimbursement.exchangeRate.url}")
	private String providerUrl;

	@Value("${reimbursement.exchangeRate.base}")
	private String base;

	@Cacheable("exchange-rates")
	public ExchangeRate getExchangeRateFrom(String date) {
		String url = generateUrl(date);

		RestTemplate restTemplate = new RestTemplate();
		ExchangeRate exchangeRate = null;

		try {
			exchangeRate = restTemplate.getForObject(url, ExchangeRate.class);
		} catch (RestClientException e) {
		}

		/*
		 * If the date argument is not valid, the server returns an HTTP status
		 * error code. This error is wrapped here.
		 */
		if (exchangeRate == null) {
			throw new IllegalArgumentException(date);
		}

		return exchangeRate;
	}

	private String generateUrl(String date) {
		return providerUrl + date + "?base=" + base;
	}

}
