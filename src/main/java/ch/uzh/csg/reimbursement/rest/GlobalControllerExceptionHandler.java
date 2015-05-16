package ch.uzh.csg.reimbursement.rest;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import ch.uzh.csg.reimbursement.dto.ErrorDto;
import ch.uzh.csg.reimbursement.model.exception.BusinessException;
import ch.uzh.csg.reimbursement.model.exception.ServiceException;

@ControllerAdvice
public class GlobalControllerExceptionHandler {

	private Logger logger = LoggerFactory.getLogger(GlobalControllerExceptionHandler.class);

	@ResponseStatus(BAD_REQUEST)
	@ExceptionHandler(RuntimeException.class)
	@ResponseBody ErrorDto handleRuntimeException(HttpServletRequest req, RuntimeException ex) {
		if(!(ex instanceof BusinessException)) {
			logger.error(ex.getMessage(), ex);
			ex = new ServiceException();
		}
		return new ErrorDto(ex);
	}
}
