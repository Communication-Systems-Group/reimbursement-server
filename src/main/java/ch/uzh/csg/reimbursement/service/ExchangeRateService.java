package ch.uzh.csg.reimbursement.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private static final Logger LOG = LoggerFactory.getLogger(ExchangeRateService.class);

	@Cacheable("exchange-rates")
	public ExchangeRateDto getExchangeRateFrom(String date) {
		String url = generateUrl(date);

		RestTemplate restTemplate = new RestTemplate();
		ExchangeRateDto exchangeRateDto = null;

		try {
			exchangeRateDto = restTemplate.getForObject(url, ExchangeRateDto.class);
		} catch (RestClientException e) {
			LOG.error("Unable to retrieve exchange-rates from service", e);
		}

		/*
		 * If the date argument is not valid, the server returns an HTTP status
		 * error code. This error is wrapped here.
		 */
		if (exchangeRateDto == null) {
			throw new InvalidDateException(date);
		}

		reduceExchangeRateByTwoPercent(exchangeRateDto.getRates());
		return exchangeRateDto;
	}

	public List<String> getSupportedCurrencies() {
		ExchangeRateDto dto = getExchangeRateFrom(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
		Set<String> set = dto.getRates().keySet();

		List<String> list = new ArrayList<>();
		list.addAll(set);
		list.add(dto.getBase());
		Collections.sort(list);

		return list;
	}

	private String generateUrl(String date) {
		return providerUrl + date + "?base=" + base;
	}

	/*
	 * The exchangeRate has to be reduced because the user gets 2% more money to
	 * cover additional costs e.g. transfer costs
	 */
	private void reduceExchangeRateByTwoPercent(Map<String, Double> rates) {
		for (Map.Entry<String, Double> mapEntry : rates.entrySet()) {
			rates.put(mapEntry.getKey(), mapEntry.getValue() / 1.02);
		}
	}
}
