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

    @ExceptionHandler({ResourceNotFoundException.class, NoHandlerFoundException.class,
            NoResourceFoundException.class})
    public ModelAndView notFound(Exception exception, HttpServletRequest request) {
        return error(HttpStatus.NOT_FOUND, "Page not found",
                "The requested page or record could not be found.", request);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, MissingServletRequestParameterException.class})
    public ModelAndView invalidRequest(Exception exception, HttpServletRequest request) {
        return error(HttpStatus.BAD_REQUEST, "Check your request",
                "One of the submitted values is invalid. Check the form and try again.", request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ModelAndView invalidValue(IllegalArgumentException exception, HttpServletRequest request) {
        String message = exception.getMessage();
        return error(HttpStatus.BAD_REQUEST, "Check your request",
                message == null || message.isBlank()
                        ? "One of the submitted values is invalid. Check the form and try again."
                        : message,
                request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ModelAndView dataConflict(DataIntegrityViolationException exception, HttpServletRequest request) {
        return error(HttpStatus.CONFLICT, "Could not save these details",
                "A record with the same unique details already exists. Review the values and try again.", request);
    }

    @ExceptionHandler(DataAccessException.class)
    public ModelAndView databaseUnavailable(DataAccessException exception, HttpServletRequest request) {
        logger.error("Database operation failed for {}", request.getRequestURI(), exception);
        return error(HttpStatus.SERVICE_UNAVAILABLE, "Data is temporarily unavailable",
                "The library could not access its database. Please try again later.", request);
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView unexpectedError(Exception exception, HttpServletRequest request) {
        logger.error("Unexpected request failure for {}", request.getRequestURI(), exception);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong",
                "The request could not be completed. Please return to the catalogue and try again.", request);
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
