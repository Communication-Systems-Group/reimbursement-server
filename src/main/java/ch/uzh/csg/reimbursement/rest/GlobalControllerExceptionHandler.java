package ch.uzh.csg.reimbursement.rest;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import ch.uzh.csg.reimbursement.dto.ErrorDto;
import ch.uzh.csg.reimbursement.model.exception.BusinessException;
import ch.uzh.csg.reimbursement.model.exception.ServiceException;

@ControllerAdvice
class GlobalControllerExceptionHandler {
	@ResponseStatus(BAD_REQUEST)
	@ExceptionHandler(RuntimeException.class)
	@ResponseBody ErrorDto handleRuntimeException(HttpServletRequest req, RuntimeException ex) {
		if(!(ex instanceof BusinessException)){
			ex = new ServiceException();
		}
		return new ErrorDto(req.getRequestURL(), ex);
	}
}
