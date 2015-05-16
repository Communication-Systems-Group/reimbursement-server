package ch.uzh.csg.reimbursement.security;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class HttpLogoutSuccessHandler implements LogoutSuccessHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpLogoutSuccessHandler.class);

	private final ObjectMapper mapper;

	@Autowired
	HttpLogoutSuccessHandler(MappingJackson2HttpMessageConverter messageConverter) {
		this.mapper = messageConverter.getObjectMapper();
	}

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException {
		response.setStatus(HttpServletResponse.SC_OK);
		LOGGER.info("onLogoutSuccess for user: "+authentication.getName());
	}
}
