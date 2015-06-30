package ch.uzh.csg.reimbursement.security;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import ch.uzh.csg.reimbursement.dto.ErrorDto;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class HttpAuthenticationEntryPoint implements AuthenticationEntryPoint {
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpAuthenticationEntryPoint.class);
	private final ObjectMapper mapper;
	@Autowired
	HttpAuthenticationEntryPoint(MappingJackson2HttpMessageConverter messageConverter) {
		this.mapper = messageConverter.getObjectMapper();
	}

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		PrintWriter writer = response.getWriter();
		mapper.writeValue(writer, new ErrorDto(authException));
		writer.flush();
		//To use default error page
		//response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
		LOGGER.info("A call from: **remoteAddr: "+ request.getRemoteAddr()+" remoteHost:"+request.getRemoteHost()+"** to the protected resource: **"+request.getRequestURI()+"** has been blocked.");
	}
}