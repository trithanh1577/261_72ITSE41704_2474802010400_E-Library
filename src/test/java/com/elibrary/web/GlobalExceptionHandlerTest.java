package com.elibrary.web;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.context.support.StaticMessageSource;
import java.util.Locale;

class GlobalExceptionHandlerTest {

    @Test
    void databaseFailureReturnsSafeServiceUnavailablePage() {
        StaticMessageSource source = new StaticMessageSource();
        source.addMessage("error.databaseTitle", Locale.ENGLISH, "Data is temporarily unavailable");
        source.addMessage("error.databaseTitle", Locale.US, "Data is temporarily unavailable");
        source.addMessage("error.databaseUnavailable", Locale.ENGLISH,
                "The service is temporarily unavailable. Please try again shortly.");
        source.addMessage("error.databaseUnavailable", Locale.US,
                "The service is temporarily unavailable. Please try again shortly.");
        GlobalExceptionHandler handler = new GlobalExceptionHandler(new UiMessages(source));
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/books");

        var response = handler.databaseUnavailable(
                new DataAccessResourceFailureException("private database connection detail"), request);

        assertThat(response.getViewName()).isEqualTo("error/error");
        assertThat(response.getStatus()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(response.getModel().get("message")).isEqualTo(
                "The service is temporarily unavailable. Please try again shortly.");
        assertThat(response.getModel().toString()).doesNotContain("private database connection detail");
    }
}
