package com.elibrary.web;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;

class GlobalExceptionHandlerTest {

    @Test
    void databaseFailureReturnsSafeServiceUnavailablePage() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/books");

        var response = handler.databaseUnavailable(
                new DataAccessResourceFailureException("private database connection detail"), request);

        assertThat(response.getViewName()).isEqualTo("error/error");
        assertThat(response.getStatus()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(response.getModel().get("message")).isEqualTo(
                "The library could not access its database. Please try again later.");
        assertThat(response.getModel().toString()).doesNotContain("private database connection detail");
    }
}
