package com.elibrary.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.servlet.http.Cookie;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class LanguageSwitchIntegrationTest {

    @Autowired private MockMvc mockMvc;

    @Test
    void languageLinksKeepSearchParametersAndPreferencePersistsInCookie() throws Exception {
        MvcResult vietnamesePage = mockMvc.perform(get("/books?keyword=Java&page=0&lang=vi"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("<html lang=\"vi\"")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Tìm sách")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "href=\"/books?keyword=Java&amp;page=0&amp;lang=en\"")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "href=\"/books?keyword=Java&amp;page=0&amp;lang=vi\"")))
                .andReturn();

        Cookie localeCookie = vietnamesePage.getResponse().getCookie("ELIBRARY_LOCALE");
        assertThat(localeCookie).isNotNull();
        assertThat(localeCookie.getValue()).isEqualTo("vi");
        assertThat(localeCookie.getMaxAge()).isEqualTo(365 * 24 * 60 * 60);

        mockMvc.perform(get("/login").cookie(localeCookie))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("<html lang=\"vi\"")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Đăng nhập")));
    }

    @Test
    void registrationValidationUsesSelectedVietnameseLocale() throws Exception {
        MvcResult registrationPage = mockMvc.perform(get("/register").param("lang", "vi"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Tạo tài khoản thành viên")))
                .andReturn();
        Cookie localeCookie = registrationPage.getResponse().getCookie("ELIBRARY_LOCALE");
        MockHttpSession session = (MockHttpSession) registrationPage.getRequest().getSession(false);
        Matcher token = Pattern.compile("name=\"_csrf\"[^>]*value=\"([^\"]+)\"")
                .matcher(registrationPage.getResponse().getContentAsString());
        assertThat(token.find()).isTrue();

        mockMvc.perform(post("/register")
                        .session(session)
                        .cookie(localeCookie)
                        .param("_csrf", token.group(1))
                        .param("displayName", "Vietnamese Reader")
                        .param("username", "invalid username")
                        .param("email", "reader@example.com")
                        .param("password", "a")
                        .param("confirmPassword", "a"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "Chỉ dùng chữ cái, chữ số, dấu chấm, gạch dưới hoặc gạch nối.")));
    }
}
