package ch.uzh.csg.reimbursement.server.dto;

import java.util.Map;

import lombok.Getter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExchangeRateDto {

	private String base;
	private String date;
	private Map<String, Double> rates;

}