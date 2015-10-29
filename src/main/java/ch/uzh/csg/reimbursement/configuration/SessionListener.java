package ch.uzh.csg.reimbursement.configuration;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionListener implements HttpSessionListener {

	private static final Logger LOG = LoggerFactory.getLogger(SessionListener.class);

	@Override
	public void sessionCreated(HttpSessionEvent event) {
		LOG.debug("Session is created | Id: " + event.getSession().getId());
		// event.getSession().setMaxInactiveInterval(5*60);
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		LOG.debug("Session is destroyed | Id: " + event.getSession().getId());
	}
}