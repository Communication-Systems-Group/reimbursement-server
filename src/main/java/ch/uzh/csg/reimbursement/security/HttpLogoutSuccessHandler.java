package ch.uzh.csg.reimbursement.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class HttpLogoutSuccessHandler implements LogoutSuccessHandler {
	private static final Logger LOG = LoggerFactory.getLogger(HttpLogoutSuccessHandler.class);

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		response.setStatus(HttpServletResponse.SC_OK);
		if(authentication != null) {
			LOG.debug("onLogoutSuccess for user: " + authentication.getName());
		}
		else {
			LOG.debug("onLogoutSuccess: User was not logged in before.");
		}
	}
}
