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
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.uzh.csg.reimbursement.dto.ErrorDto;

@Component
public class HttpAuthenticationEntryPoint implements AuthenticationEntryPoint {
	private static final Logger LOG = LoggerFactory.getLogger(HttpAuthenticationEntryPoint.class);

	@Autowired
        private ObjectMapper mapper;

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		PrintWriter writer = response.getWriter();
		mapper.writeValue(writer, new ErrorDto(authException));
		writer.flush();

		//To use default error page
		//response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());

		LOG.debug("A call from: remoteHost:" + request.getRemoteHost() + " to the protected resource: "
				+ request.getRequestURI() + " has been blocked.");
	}
}