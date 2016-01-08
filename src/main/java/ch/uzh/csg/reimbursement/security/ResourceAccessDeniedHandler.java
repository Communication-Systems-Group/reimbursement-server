package ch.uzh.csg.reimbursement.security;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.uzh.csg.reimbursement.dto.ErrorDto;

@Component
public class ResourceAccessDeniedHandler implements AccessDeniedHandler {

	private static final Logger LOG = LoggerFactory.getLogger(ResourceAccessDeniedHandler.class);
	private final ObjectMapper mapper;

	@Autowired
	ResourceAccessDeniedHandler(ObjectMapper mapper) {
		this.mapper = mapper;
	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
			throws IOException, ServletException {

		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		PrintWriter writer = response.getWriter();
		mapper.writeValue(writer, new ErrorDto(accessDeniedException));
		writer.flush();

		LOG.debug("Access denied: " + accessDeniedException.getMessage());
	}

}
