package com.elibrary.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import com.elibrary.domain.Book;
import com.elibrary.domain.BookCopy;
import com.elibrary.domain.CopyStatus;
import com.elibrary.domain.FineSettlementType;
import com.elibrary.domain.Loan;
import com.elibrary.domain.ReservationStatus;
import com.elibrary.domain.Role;
import com.elibrary.domain.UserAccount;
import com.elibrary.repository.BookCopyRepository;
import com.elibrary.repository.BookRepository;
import com.elibrary.repository.BorrowRequestRepository;
import com.elibrary.repository.FineSettlementRepository;
import com.elibrary.repository.LoanRepository;
import com.elibrary.repository.ReservationRepository;
import com.elibrary.repository.UserAccountRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class LibraryWorkflowIntegrationTest {

    private static final String TEST_PASSWORD = "integration-password";

    @Autowired private MockMvc mockMvc;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private UserAccountRepository userRepository;
    @Autowired private BookRepository bookRepository;
    @Autowired private BookCopyRepository copyRepository;
    @Autowired private BorrowRequestRepository requestRepository;
    @Autowired private LoanRepository loanRepository;
    @Autowired private ReservationRepository reservationRepository;
    @Autowired private FineSettlementRepository settlementRepository;

    @Test
    void memberRequestLibrarianApprovalAndReturnCompleteTheBorrowingJourney() throws Exception {
        UserAccount member = createMember("borrower");
        Book book = createBook("Circulation Journey");
        BookCopy copy = copyRepository.save(new BookCopy("COPY-" + suffix(), book));

        TestSession memberSession = signIn(member.getUsername(), TEST_PASSWORD);
        mockMvc.perform(get("/books/{id}", book.getId()).session(memberSession.session()))
                .andExpect(status().isOk());
        mockMvc.perform(post("/books/{id}/requests", book.getId())
                        .session(memberSession.session())
                        .param("_csrf", memberSession.csrf())
                        .param("note", "Needed for the course project"))
                .andExpect(status().is3xxRedirection());

        var request = requestRepository.findByMemberUsernameOrderByRequestedAtDesc(member.getUsername())
                .get(0);
        assertThat(request.getStatus().name()).isEqualTo("PENDING");

        TestSession librarianSession = signIn("admin", "admin123");
        mockMvc.perform(post("/circulation/requests/{id}/approve", request.getId())
                        .session(librarianSession.session())
                        .param("_csrf", librarianSession.csrf()))
                .andExpect(status().is3xxRedirection());

        Loan loan = loanRepository.findAllByOrderByIssuedAtDesc().stream()
                .filter(item -> item.getRequest() != null && item.getRequest().getId().equals(request.getId()))
                .findFirst().orElseThrow();
        assertThat(loan.isActive()).isTrue();
        assertThat(loan.getCopy().getStatus()).isEqualTo(CopyStatus.ON_LOAN);
        mockMvc.perform(get("/circulation/mine").session(memberSession.session()))
                .andExpect(status().isOk());

        mockMvc.perform(post("/circulation/loans/{id}/return", loan.getId())
                        .session(librarianSession.session())
                        .param("_csrf", librarianSession.csrf()))
                .andExpect(status().is3xxRedirection());

        Loan returnedLoan = loanRepository.findById(loan.getId()).orElseThrow();
        assertThat(returnedLoan.isActive()).isFalse();
        assertThat(copyRepository.findById(copy.getId()).orElseThrow().getStatus())
                .isEqualTo(CopyStatus.AVAILABLE);
    }

    @Test
    void reservationClaimAndFinePaymentCompleteTheQueueJourney() throws Exception {
        UserAccount waitingMember = createMember("queue-member");
        UserAccount overdueMember = createMember("overdue-member");
        Book book = createBook("Reservation Journey");
        BookCopy copy = new BookCopy("COPY-" + suffix(), book);
        copy.setStatus(CopyStatus.ON_LOAN);
        copyRepository.save(copy);
        Loan overdueLoan = loanRepository.save(new Loan(overdueMember, copy, null,
                LocalDateTime.now().minusDays(3), LocalDate.now().minusDays(1)));

        TestSession memberSession = signIn(waitingMember.getUsername(), TEST_PASSWORD);
        mockMvc.perform(post("/books/{id}/reservations", book.getId())
                        .session(memberSession.session())
                        .param("_csrf", memberSession.csrf()))
                .andExpect(status().is3xxRedirection());
        var reservation = reservationRepository
                .findByMemberUsernameOrderByReservedAtDesc(waitingMember.getUsername()).get(0);
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.WAITING);

        TestSession librarianSession = signIn("admin", "admin123");
        mockMvc.perform(post("/circulation/loans/{id}/return", overdueLoan.getId())
                        .session(librarianSession.session())
                        .param("_csrf", librarianSession.csrf()))
                .andExpect(status().is3xxRedirection());
        assertThat(reservationRepository.findById(reservation.getId()).orElseThrow().getStatus())
                .isEqualTo(ReservationStatus.READY);
        Loan returnedLoan = loanRepository.findById(overdueLoan.getId()).orElseThrow();
        assertThat(returnedLoan.getFineAmount()).isEqualByComparingTo("5000.00");

        mockMvc.perform(post("/reservations/{id}/claim", reservation.getId())
                        .session(memberSession.session())
                        .param("_csrf", memberSession.csrf()))
                .andExpect(status().is3xxRedirection());
        assertThat(reservationRepository.findById(reservation.getId()).orElseThrow().getStatus())
                .isEqualTo(ReservationStatus.FULFILLED);

        mockMvc.perform(get("/circulation/loans").session(librarianSession.session())
                        .param("query", overdueMember.getUsername()))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(book.getTitle())));
        mockMvc.perform(post("/circulation/loans/{id}/fine-settlements", overdueLoan.getId())
                        .session(librarianSession.session())
                        .param("_csrf", librarianSession.csrf())
                        .param("type", FineSettlementType.PAID.name())
                        .param("note", "Paid at the desk"))
                .andExpect(status().is3xxRedirection());

        assertThat(settlementRepository.sumAmountByLoanId(overdueLoan.getId()))
                .isEqualByComparingTo("5000.00");
        var settlement = settlementRepository.findByLoanIdOrderByRecordedAtAsc(overdueLoan.getId()).get(0);
        assertThat(settlement.getType()).isEqualTo(FineSettlementType.PAID);
        assertThat(settlement.getProcessedBy()).isEqualTo("admin");
        assertThat(settlement.getNote()).isEqualTo("Paid at the desk");
        Loan settledLoan = loanRepository.findAllByOrderByIssuedAtDesc().stream()
                .filter(item -> item.getId().equals(overdueLoan.getId())).findFirst().orElseThrow();
        assertThat(settledLoan.hasOutstandingFine()).isFalse();
    }

    private UserAccount createMember(String prefix) {
        String username = prefix + "-" + suffix();
        return userRepository.save(new UserAccount(username, username + "@example.test",
                passwordEncoder.encode(TEST_PASSWORD), "Library Test Member", Role.MEMBER));
    }

    private Book createBook(String title) {
        String isbn = "IT-" + UUID.randomUUID().toString().replace("-", "").substring(0, 20);
        return bookRepository.save(new Book(title + " " + suffix(), isbn,
                "Course Project Author", "Project Testing", "Integration-test catalogue fixture."));
    }

    private String suffix() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private TestSession signIn(String username, String password) throws Exception {
        MvcResult loginPage = mockMvc.perform(get("/login")).andExpect(status().isOk()).andReturn();
        Matcher matcher = Pattern.compile("name=\"_csrf\"[^>]*value=\"([^\"]+)\"")
                .matcher(loginPage.getResponse().getContentAsString());
        if (!matcher.find()) throw new AssertionError("Login form did not render a CSRF token.");
        String csrf = matcher.group(1);
        MockHttpSession session = (MockHttpSession) loginPage.getRequest().getSession(false);
        mockMvc.perform(post("/login").session(session)
                        .param("_csrf", csrf)
                        .param("username", username)
                        .param("password", password))
                .andExpect(status().is3xxRedirection());
        MvcResult authenticatedPage = mockMvc.perform(get("/login").session(session))
                .andExpect(status().isOk()).andReturn();
        Matcher refreshedToken = Pattern.compile("name=\"_csrf\"[^>]*value=\"([^\"]+)\"")
                .matcher(authenticatedPage.getResponse().getContentAsString());
        if (!refreshedToken.find()) throw new AssertionError("Login form did not render a refreshed CSRF token.");
        return new TestSession(session, refreshedToken.group(1));
    }

    private record TestSession(MockHttpSession session, String csrf) { }
}
