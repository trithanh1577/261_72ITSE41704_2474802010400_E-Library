package com.elibrary.config;

import com.elibrary.domain.Book;
import com.elibrary.domain.BookCopy;
import com.elibrary.domain.Role;
import com.elibrary.domain.UserAccount;
import com.elibrary.repository.BookCopyRepository;
import com.elibrary.repository.BookRepository;
import com.elibrary.repository.UserAccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedData(UserAccountRepository userRepository,
                               BookRepository bookRepository,
                               BookCopyRepository copyRepository,
                               PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {
                userRepository.save(new UserAccount("admin", passwordEncoder.encode("admin123"), "Demo Librarian", Role.LIBRARIAN));
                userRepository.save(new UserAccount("member", passwordEncoder.encode("member123"), "Demo Member", Role.MEMBER));
            }
            if (bookRepository.count() == 0) {
                Book cleanCode = bookRepository.save(new Book("Clean Code", "9780132350884", "Robert C. Martin", "Software", "A practical introduction to readable and maintainable code."));
                Book domainDriven = bookRepository.save(new Book("Domain-Driven Design", "9780321125217", "Eric Evans", "Software", "A reference for modelling complex business domains."));
                Book java = bookRepository.save(new Book("Effective Java", "9780134685991", "Joshua Bloch", "Programming", "Patterns and practices for modern Java development."));
                copyRepository.save(new BookCopy("CC-001", cleanCode));
                copyRepository.save(new BookCopy("CC-002", cleanCode));
                copyRepository.save(new BookCopy("DDD-001", domainDriven));
                copyRepository.save(new BookCopy("JAVA-001", java));
            }
        };
    }
}
