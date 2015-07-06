package ch.uzh.csg.reimbursement.security;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import ch.uzh.csg.reimbursement.dto.ErrorDto;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class FormLoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(FormLoginFailureHandler.class);

	private final ObjectMapper mapper;

	@Autowired
	FormLoginFailureHandler(ObjectMapper mapper) {
		this.mapper = mapper;
	}


	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException ex) throws IOException, ServletException {

		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("application/json");
		PrintWriter writer = response.getWriter();
		mapper.writeValue(writer, new ErrorDto(ex));
		writer.flush();

		LOGGER.info("Authentication failed: "+ex.getMessage());
	}
}
