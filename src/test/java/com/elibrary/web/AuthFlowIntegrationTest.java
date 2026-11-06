package com.elibrary.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.elibrary.domain.Role;
import com.elibrary.domain.UserAccount;
import com.elibrary.repository.UserAccountRepository;
import com.elibrary.service.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootTest
@AutoConfigureMockMvc
class AuthFlowIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserAccountRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private CustomUserDetailsService userDetailsService;

    @Test
    void registrationAcceptsShortPasswordAndBothIdentifiersCanSignIn() throws Exception {
        MvcResult registrationPage = mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Email")))
                .andReturn();
        MockHttpSession session = (MockHttpSession) registrationPage.getRequest().getSession(false);
        String csrf = csrfToken(registrationPage);
        mockMvc.perform(get("/circulation/loans").session(session))
                .andExpect(status().is3xxRedirection());
        mockMvc.perform(get("/login").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Username or email")));
        mockMvc.perform(get("/books").param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Find a book")));

        String overLimitPassword = "ệ".repeat(25);
        mockMvc.perform(post("/register")
                        .session(session)
                        .param("_csrf", csrf)
                        .param("displayName", "Unicode Reader")
                        .param("username", "UnicodeReader")
                        .param("email", "unicode@example.com")
                        .param("password", overLimitPassword)
                        .param("confirmPassword", overLimitPassword))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("72-byte UTF-8 input limit")));
        assertThat(userRepository.existsByUsernameKey(UserAccount.usernameKeyFor("UnicodeReader"))).isFalse();

        mockMvc.perform(post("/register")
                        .session(session)
                        .param("_csrf", csrf)
                        .param("displayName", "Integration Reader")
                        .param("username", "ReaderCase")
                        .param("email", "ReaderCase@example.com")
                        .param("password", "Tri")
                        .param("confirmPassword", "Tri"))
                .andExpect(status().is3xxRedirection());

        UserAccount account = userRepository.findByUsernameKey(UserAccount.usernameKeyFor("ReaderCase"))
                .orElseThrow();
        assertThat(account.getRole()).isEqualTo(Role.MEMBER);
        assertThat(account.getPassword()).startsWith("$2").isNotEqualTo("Tri");
        assertThat(passwordEncoder.matches("Tri", account.getPassword())).isTrue();

        assertThat(userDetailsService.loadUserByUsername("ReaderCase").getUsername())
                .isEqualTo("ReaderCase");
        org.assertj.core.api.Assertions.assertThatThrownBy(
                        () -> userDetailsService.loadUserByUsername("readercase"))
                .isInstanceOf(UsernameNotFoundException.class);
        assertThat(userDetailsService.loadUserByUsername("ReaderCase@EXAMPLE.com").getUsername())
                .isEqualTo("ReaderCase");

        MvcResult loginPage = mockMvc.perform(get("/login").session(session)).andReturn();
        mockMvc.perform(post("/login")
                        .session(session)
                        .param("_csrf", csrfToken(loginPage))
                        .param("username", "ReaderCase@EXAMPLE.COM")
                        .param("password", "Tri"))
                .andExpect(status().is3xxRedirection());
        mockMvc.perform(get("/circulation/mine").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("My activity")));
    }

    private String csrfToken(MvcResult page) throws Exception {
        Matcher matcher = Pattern.compile("name=\"_csrf\"[^>]*value=\"([^\"]+)\"")
                .matcher(page.getResponse().getContentAsString());
        if (!matcher.find()) throw new AssertionError("Form did not render a CSRF token.");
        return matcher.group(1);
    }
}
