package com.elibrary.web;

import com.elibrary.service.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.bind.MissingServletRequestParameterException;

@ControllerAdvice(annotations = Controller.class)
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final UiMessages messages;

    public GlobalExceptionHandler(UiMessages messages) {
        this.messages = messages;
    }

    @ExceptionHandler({ResourceNotFoundException.class, NoHandlerFoundException.class,
            NoResourceFoundException.class})
    public ModelAndView notFound(Exception exception, HttpServletRequest request) {
        return error(HttpStatus.NOT_FOUND, messages.get("error.pageNotFound"),
                messages.get("error.pageNotFoundMessage"), request);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, MissingServletRequestParameterException.class})
    public ModelAndView invalidRequest(Exception exception, HttpServletRequest request) {
        return error(HttpStatus.BAD_REQUEST, messages.get("error.checkRequest"),
                messages.get("error.invalidRequest"), request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ModelAndView invalidValue(IllegalArgumentException exception, HttpServletRequest request) {
        String message = exception.getMessage();
        return error(HttpStatus.BAD_REQUEST, messages.get("error.checkRequest"),
                messages.fromService(message), request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ModelAndView dataConflict(DataIntegrityViolationException exception, HttpServletRequest request) {
        return error(HttpStatus.CONFLICT, messages.get("error.saveConflict"),
                messages.get("error.conflictMessage"), request);
    }

    @ExceptionHandler(DataAccessException.class)
    public ModelAndView databaseUnavailable(DataAccessException exception, HttpServletRequest request) {
        logger.error("Database operation failed for {}", request.getRequestURI(), exception);
        return error(HttpStatus.SERVICE_UNAVAILABLE, messages.get("error.databaseTitle"),
                messages.get("error.databaseUnavailable"), request);
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView unexpectedError(Exception exception, HttpServletRequest request) {
        logger.error("Unexpected request failure for {}", request.getRequestURI(), exception);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, messages.get("error.unexpectedTitle"),
                messages.get("error.unexpectedMessage"), request);
    }

    private ModelAndView error(HttpStatus status, String title, String message, HttpServletRequest request) {
        ModelAndView view = new ModelAndView("error/error");
        view.setStatus(status);
        view.addObject("status", status.value());
        view.addObject("title", title);
        view.addObject("message", message);
        view.addObject("path", request.getRequestURI());
        return view;
    }
}
