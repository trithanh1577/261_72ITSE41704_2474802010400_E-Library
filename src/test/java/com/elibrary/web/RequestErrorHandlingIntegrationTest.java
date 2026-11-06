package com.elibrary.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;

import com.elibrary.domain.Book;
import com.elibrary.domain.BookCopy;
import com.elibrary.repository.BookCopyRepository;
import com.elibrary.repository.BookRepository;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.MockMvc;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
class RequestErrorHandlingIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private BookRepository bookRepository;
    @Autowired private BookCopyRepository copyRepository;

    @Test
    void invalidPageNumberShowsFriendlyBadRequestPage() throws Exception {
        mockMvc.perform(get("/books").param("page", "not-a-number"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("One of the submitted values is invalid")));
    }

    @Test
    void missingBookShowsNotFoundPage() throws Exception {
        mockMvc.perform(get("/books/999999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Page not found")));
    }

    @Test
    void memberCannotOpenLibrarianLoanReport() throws Exception {
        MockHttpSession session = signIn("member", "member123");
        mockMvc.perform(get("/circulation/loans").session(session))
                .andExpect(status().isForbidden())
                .andExpect(forwardedUrl("/error/403"));
    }

    @Test
    void memberCannotOpenMemberManagement() throws Exception {
        MockHttpSession session = signIn("member", "member123");
        mockMvc.perform(get("/members").session(session))
                .andExpect(status().isForbidden())
                .andExpect(forwardedUrl("/error/403"));
    }

    @Test
    void memberCannotSubmitFineSettlement() throws Exception {
        MockHttpSession session = signIn("member", "member123");
        String csrf = csrfToken(session);
        mockMvc.perform(post("/circulation/loans/1/fine-settlements")
                        .session(session)
                        .param("_csrf", csrf)
                        .param("type", "PAID")
                        .param("note", "Payment test"))
                .andExpect(status().isForbidden())
                .andExpect(forwardedUrl("/error/403"));
    }

    @Test
    void librarianCanOpenMemberManagement() throws Exception {
        MockHttpSession session = signIn("admin", "admin123");
        mockMvc.perform(get("/members").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Members")));
    }

    @Test
    void memberCannotOpenLibrarianBookForm() throws Exception {
        MockHttpSession session = signIn("member", "member123");
        mockMvc.perform(get("/books/new").session(session))
                .andExpect(status().isForbidden())
                .andExpect(forwardedUrl("/error/403"));
    }

    @Test
    void logoutEndsTheAuthenticatedSession() throws Exception {
        MockHttpSession session = signIn("member", "member123");
        mockMvc.perform(post("/logout").session(session).param("_csrf", csrfToken(session)))
                .andExpect(status().is3xxRedirection())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers
                        .redirectedUrl("/login?logout"));
        mockMvc.perform(get("/circulation/mine").session(session))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void blankBookTitleShowsValidationMessageWithoutSaving() throws Exception {
        MockHttpSession session = signIn("admin", "admin123");
        String isbn = "TEST-" + UUID.randomUUID();
        mockMvc.perform(post("/books/save").session(session)
                        .param("_csrf", csrfToken(session))
                        .param("title", "")
                        .param("isbn", isbn)
                        .param("author", "Test Author")
                        .param("category", "Testing"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Title is required")));
        org.assertj.core.api.Assertions.assertThat(bookRepository.findByIsbn(isbn)).isEmpty();
    }

    @Test
    void overlongBookTitleShowsValidationMessageWithoutSaving() throws Exception {
        MockHttpSession session = signIn("admin", "admin123");
        String isbn = "TEST-" + UUID.randomUUID();
        mockMvc.perform(post("/books/save").session(session)
                        .param("_csrf", csrfToken(session))
                        .param("title", "T".repeat(181))
                        .param("isbn", isbn)
                        .param("author", "Test Author")
                        .param("category", "Testing"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Title must be at most 180 characters")));
        org.assertj.core.api.Assertions.assertThat(bookRepository.findByIsbn(isbn)).isEmpty();
    }

    @Test
    void duplicateCopyCodeShowsMessageWithoutCreatingAnotherCopy() throws Exception {
        MockHttpSession session = signIn("admin", "admin123");
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 20);
        Book book = bookRepository.save(new Book("Validation Book " + suffix, "V-" + suffix,
                "Test Author", "Testing", "Integration-test fixture."));
        String copyCode = "COPY-" + suffix;
        copyRepository.save(new BookCopy(copyCode, book));

        mockMvc.perform(post("/books/{bookId}/copies", book.getId()).session(session)
                        .param("_csrf", csrfToken(session))
                        .param("copyCode", copyCode)
                        .param("status", "AVAILABLE"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Copy code already exists.")));
        org.assertj.core.api.Assertions.assertThat(copyRepository.findByBookIdOrderByCopyCode(book.getId()))
                .hasSize(1);
    }

    private MockHttpSession signIn(String username, String password) throws Exception {
        MvcResult loginPage = mockMvc.perform(get("/login")).andExpect(status().isOk()).andReturn();
        String csrf = Pattern.compile("name=\"_csrf\"[^>]*value=\"([^\"]+)\"")
                .matcher(loginPage.getResponse().getContentAsString())
                .results()
                .map(match -> match.group(1))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Login form did not render a CSRF token."));
        MockHttpSession session = (MockHttpSession) loginPage.getRequest().getSession(false);
        MvcResult response = mockMvc.perform(post("/login")
                        .session(session)
                        .param("_csrf", csrf)
                        .param("username", username)
                        .param("password", password))
                .andExpect(status().is3xxRedirection())
                .andReturn();
        return (MockHttpSession) response.getRequest().getSession(false);
    }

    private String csrfToken(MockHttpSession session) throws Exception {
        String html = mockMvc.perform(get("/login").session(session))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Matcher matcher = Pattern.compile("name=\"_csrf\"[^>]*value=\"([^\"]+)\"").matcher(html);
        if (!matcher.find()) throw new AssertionError("Login form did not render a CSRF token.");
        return matcher.group(1);
    }
}
