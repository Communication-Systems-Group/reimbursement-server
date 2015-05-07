package ch.uzh.csg.reimbursement.rest;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import ch.uzh.csg.reimbursement.dto.ErrorDto;
import ch.uzh.csg.reimbursement.model.exception.SignatureCroppingException;

@ControllerAdvice
class GlobalControllerExceptionHandler {

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(RuntimeException.class)
	@ResponseBody ErrorDto handleRuntimeException(HttpServletRequest req, RuntimeException ex) {
		return new ErrorDto(req.getRequestURL(), ex);
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(SignatureCroppingException.class)
	@ResponseBody ErrorDto handleCroppingException(HttpServletRequest req, RuntimeException ex) {
		return new ErrorDto(req.getRequestURL(), ex);
	}

	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(NullPointerException.class)
	@ResponseBody ErrorDto handleNullPointerException(HttpServletRequest req, RuntimeException ex) {
		return new ErrorDto(req.getRequestURL(), ex);
	}

}
