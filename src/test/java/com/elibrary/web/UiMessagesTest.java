package com.elibrary.web;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;

class UiMessagesTest {

    @AfterEach
    void resetLocale() {
        LocaleContextHolder.resetLocaleContext();
    }

    @Test
    void serviceNotFoundAndAvailabilityErrorsUseVietnameseMessages() {
        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasename("messages");
        source.setDefaultEncoding("UTF-8");
        UiMessages messages = new UiMessages(source);
        LocaleContextHolder.setLocale(Locale.forLanguageTag("vi"));

        assertThat(messages.fromService("Active book was not found."))
                .isEqualTo("Không tìm thấy sách đang hoạt động.");
        assertThat(messages.fromService("Borrowing request was not found."))
                .isEqualTo("Không tìm thấy yêu cầu mượn.");
        assertThat(messages.fromService("No available copy remains for this book."))
                .isEqualTo("Không còn bản sao khả dụng của sách này.");
        assertThat(messages.fromService("Loan was not found."))
                .isEqualTo("Không tìm thấy phiếu mượn.");
        assertThat(messages.fromService("Reservation was not found."))
                .isEqualTo("Không tìm thấy lượt đặt trước.");
        assertThat(messages.fromService("Assessed fine must be zero or greater."))
                .isEqualTo("Giá trị đã gửi không hợp lệ. Hãy kiểm tra biểu mẫu và thử lại.");
    }
}
