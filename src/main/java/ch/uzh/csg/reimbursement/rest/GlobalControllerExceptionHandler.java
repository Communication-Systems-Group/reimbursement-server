package ch.uzh.csg.reimbursement.rest;

import static org.springframework.http.HttpStatus.BAD_REQUEST; //400
import static org.springframework.http.HttpStatus.FORBIDDEN; //403
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR; //500
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED; //405
import static org.springframework.http.HttpStatus.NOT_FOUND; //404
import static org.springframework.http.HttpStatus.UNSUPPORTED_MEDIA_TYPE; //415

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.NestedRuntimeException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

import ch.uzh.csg.reimbursement.dto.ErrorDto;
import ch.uzh.csg.reimbursement.model.exception.AccessViolationException;
import ch.uzh.csg.reimbursement.model.exception.BusinessException;
import ch.uzh.csg.reimbursement.model.exception.ServiceException;

@ControllerAdvice
public class GlobalControllerExceptionHandler {

	private Logger logger = LoggerFactory.getLogger(GlobalControllerExceptionHandler.class);

	// General
	@ExceptionHandler(RuntimeException.class)
	@ResponseBody
	public ResponseEntity<ErrorDto> handleRuntimeException(HttpServletRequest req, RuntimeException ex) {
		if (!(ex instanceof BusinessException) && !(ex instanceof AccessDeniedException)) {
			logger.error(ex.getMessage(), ex);
			ex = new ServiceException();
		}
		return new ResponseEntity<ErrorDto>(new ErrorDto(ex), BAD_REQUEST);
	}

	// 403
	@ExceptionHandler(AccessViolationException.class)
	@ResponseBody
	public ResponseEntity<ErrorDto> statusCodeChangeAccessViolationException(HttpServletRequest req, AccessViolationException ex) {
		logger.info("Changed response status code of AccessViolationException");
		return new ResponseEntity<ErrorDto>(new ErrorDto(ex), FORBIDDEN);
	}

	// 404
	@ExceptionHandler(NoHandlerFoundException.class)
	@ResponseBody
	public ResponseEntity<ErrorDto> requestHandlingNoHandlerFound(HttpServletRequest req, NoHandlerFoundException ex) {
		logger.info("NoHandlerFoundException transformed to ServiceException: " + ex.getMessage());
		return new ResponseEntity<ErrorDto>(new ErrorDto(new ServiceException()), NOT_FOUND);
	}

	@ExceptionHandler(NoSuchRequestHandlingMethodException.class)
	@ResponseBody
	public ResponseEntity<ErrorDto> noSuchRequestHandlingMethod(HttpServletRequest req,
			NoSuchRequestHandlingMethodException ex) {
		logger.info("NoSuchRequestHandlingMethodException transformed to ServiceException: " + ex.getMessage());
		return new ResponseEntity<ErrorDto>(new ErrorDto(new ServiceException()), NOT_FOUND);
	}

	// 405
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	@ResponseBody
	public ResponseEntity<ErrorDto> httpRequestMethodNotSupported(HttpServletRequest req,
			HttpRequestMethodNotSupportedException ex) {
		logger.info("HttpRequestMethodNotSupportedException transformed to ServiceException: " + ex.getMessage());
		return new ResponseEntity<ErrorDto>(new ErrorDto(new ServiceException()), METHOD_NOT_ALLOWED);
	}

	// 415
	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	@ResponseBody
	public ResponseEntity<ErrorDto> unsuportedMediaType(HttpServletRequest req, HttpMediaTypeNotSupportedException ex) {
		logger.info("HttpMediaTypeNotSupportedException transformed to ServiceException: " + ex.getMessage());
		return new ResponseEntity<ErrorDto>(new ErrorDto(new ServiceException()), UNSUPPORTED_MEDIA_TYPE);
	}

	// 415
	@ExceptionHandler({ MissingServletRequestParameterException.class, ServletRequestBindingException.class,
		TypeMismatchException.class, HttpMessageNotReadableException.class, MethodArgumentNotValidException.class,
		MissingServletRequestPartException.class })
	@ResponseBody
	public ResponseEntity<ErrorDto> badRequest(HttpServletRequest req, Exception ex) {
		logger.info("Exception transformed to ServiceException: " + ex.getMessage());
		return new ResponseEntity<ErrorDto>(new ErrorDto(new ServiceException()), BAD_REQUEST);
	}

	// 500
	@ExceptionHandler({ HttpMessageNotWritableException.class, ConversionNotSupportedException.class })
	@ResponseBody
	public ResponseEntity<ErrorDto> internalServerError(HttpServletRequest req, NestedRuntimeException ex) {
		logger.info("NestedRuntimeException transformed to ServiceException: " + ex.getMessage());
		return new ResponseEntity<ErrorDto>(new ErrorDto(new ServiceException()), INTERNAL_SERVER_ERROR);
	}

}