package com.elibrary.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.elibrary.domain.Book;
import com.elibrary.domain.BookCopy;
import com.elibrary.domain.BorrowRequest;
import com.elibrary.domain.BorrowRequestStatus;
import com.elibrary.domain.CopyStatus;
import com.elibrary.domain.Loan;
import com.elibrary.domain.MembershipType;
import com.elibrary.domain.Reservation;
import com.elibrary.domain.ReservationStatus;
import com.elibrary.domain.Role;
import com.elibrary.domain.UserAccount;
import com.elibrary.repository.BookCopyRepository;
import com.elibrary.repository.BookRepository;
import com.elibrary.repository.BorrowRequestRepository;
import com.elibrary.repository.LoanRepository;
import com.elibrary.repository.ReservationRepository;
import com.elibrary.repository.UserAccountRepository;
import com.elibrary.service.CirculationService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class CoreUseCaseIntegrationTest {

    private static final String TEST_PASSWORD = "integration-password";

    @Autowired private MockMvc mockMvc;
    @Autowired private UserAccountRepository userRepository;
    @Autowired private BookRepository bookRepository;
    @Autowired private BookCopyRepository copyRepository;
    @Autowired private BorrowRequestRepository requestRepository;
    @SpyBean private ReservationRepository reservationRepository;
    @Autowired private LoanRepository loanRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private CirculationService circulationService;
    @PersistenceContext private EntityManager entityManager;

    @Test
    void concurrentDuplicateBorrowRequestsCreateOnlyOnePendingRequest() throws Exception {
        UserAccount member = createMember("concurrent-requester");
        Book book = createBook("Concurrent Request Title");
        CountDownLatch startTogether = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(2);

        try {
            List<Future<Boolean>> submissions = List.of(
                    executor.submit(() -> submitAfter(startTogether, member, book)),
                    executor.submit(() -> submitAfter(startTogether, member, book)));
            startTogether.countDown();

            List<Boolean> accepted = List.of(
                    submissions.get(0).get(15, TimeUnit.SECONDS),
                    submissions.get(1).get(15, TimeUnit.SECONDS));

            assertThat(accepted).containsExactlyInAnyOrder(true, false);
            long pendingRequests = requestRepository
                    .findByMemberUsernameOrderByRequestedAtDesc(member.getUsername()).stream()
                    .filter(request -> request.getBook().getId().equals(book.getId()))
                    .filter(request -> request.getStatus() == BorrowRequestStatus.PENDING)
                    .count();
            assertThat(pendingRequests).isEqualTo(1);
        } finally {
            executor.shutdownNow();
        }
    }

    private boolean submitAfter(CountDownLatch startTogether, UserAccount member, Book book)
            throws Exception {
        if (!startTogether.await(5, TimeUnit.SECONDS)) {
            throw new IllegalStateException("Concurrent submissions did not start together.");
        }
        try {
            circulationService.submitRequest(member.getUsername(), book.getId(), null);
            return true;
        } catch (IllegalArgumentException duplicate) {
            if (!duplicate.getMessage().contains("pending request")) {
                throw duplicate;
            }
            return false;
        }
    }

    @Test
    void librarianCreatesTitleAndCopyAndMemberSearchesAvailability() throws Exception {
        String suffix = suffix();
        TestSession librarian = signIn("admin", "admin123");
        mockMvc.perform(post("/books/save")
                        .session(librarian.session())
                        .param("_csrf", librarian.csrf())
                        .param("title", "Course Design " + suffix)
                        .param("isbn", "ISBN-" + suffix)
                        .param("author", "Project Author")
                        .param("category", "Coursework")
                        .param("description", "A book added through the librarian workflow."))
                .andExpect(status().is3xxRedirection());

        Book book = bookRepository.findByIsbn("ISBN-" + suffix).orElseThrow();
        mockMvc.perform(post("/books/{bookId}/copies", book.getId())
                        .session(librarian.session())
                        .param("_csrf", librarian.csrf())
                        .param("copyCode", "COPY-" + suffix)
                        .param("status", CopyStatus.AVAILABLE.name()))
                .andExpect(status().is3xxRedirection());

        UserAccount member = createMember("catalogue-reader");
        TestSession memberSession = signIn(member.getUsername(), TEST_PASSWORD);
        mockMvc.perform(get("/books").param("keyword", "Course Design " + suffix))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(book.getTitle())));
        mockMvc.perform(get("/books/{id}", book.getId()).session(memberSession.session()))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("<strong>1</strong>")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("available copy")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("COPY-" + suffix)));
        assertThat(copyRepository.countByBookIdAndStatus(book.getId(), CopyStatus.AVAILABLE)).isEqualTo(1);
    }

    @Test
    void reservedCopyEditKeepsQueueOwnedStatusAndStillAllowsCodeCorrection() throws Exception {
        Book book = createBook("Held Reservation Copy");
        BookCopy copy = new BookCopy("COPY-" + suffix(), book);
        copy.setStatus(CopyStatus.RESERVED);
        copy = copyRepository.save(copy);
        TestSession librarian = signIn("admin", "admin123");

        String form = mockMvc.perform(get("/copies/{id}/edit", copy.getId())
                        .session(librarian.session()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThat(form).contains("held for a reservation", "type=\"hidden\"", "value=\"RESERVED\"")
                .doesNotContain("<select id=\"status\"");

        mockMvc.perform(post("/copies/{id}", copy.getId())
                        .session(librarian.session())
                        .param("_csrf", librarian.csrf())
                        .param("copyCode", "CORRECTED-" + suffix())
                        .param("status", CopyStatus.RESERVED.name()))
                .andExpect(status().is3xxRedirection());
        BookCopy updated = copyRepository.findById(copy.getId()).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(CopyStatus.RESERVED);
        assertThat(updated.getCopyCode()).startsWith("CORRECTED-");

        mockMvc.perform(post("/copies/{id}", copy.getId())
                        .session(librarian.session())
                        .param("_csrf", librarian.csrf())
                        .param("copyCode", updated.getCopyCode())
                        .param("status", CopyStatus.AVAILABLE.name()))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "reservation workflow")));
        assertThat(copyRepository.findById(copy.getId()).orElseThrow().getStatus())
                .isEqualTo(CopyStatus.RESERVED);
    }

    @Test
    void memberCannotReserveTitleWhileAnAvailableCopyExists() throws Exception {
        UserAccount member = createMember("available-copy-reader");
        Book book = createBook("Available Copy Reservation Check");
        BookCopy copy = copyRepository.save(new BookCopy("COPY-" + suffix(), book));
        TestSession memberSession = signIn(member.getUsername(), TEST_PASSWORD);

        mockMvc.perform(post("/books/{bookId}/reservations", book.getId())
                        .session(memberSession.session())
                        .param("_csrf", memberSession.csrf()))
                .andExpect(status().is3xxRedirection());

        assertThat(reservationRepository.findByMemberUsernameOrderByReservedAtDesc(member.getUsername()))
                .isEmpty();
        assertThat(copyRepository.findById(copy.getId()).orElseThrow().getStatus())
                .isEqualTo(CopyStatus.AVAILABLE);
    }

    @Test
    void librarianCannotIssueCopyHeldForAnotherMembersReadyReservation() throws Exception {
        UserAccount reservationHolder = createMember("reservation-holder");
        UserAccount requestingMember = createMember("reservation-bypass-reader");
        Book book = createBook("Reserved Copy Issue Check");
        BookCopy copy = new BookCopy("COPY-" + suffix(), book);
        copy.setStatus(CopyStatus.RESERVED);
        copy = copyRepository.save(copy);
        Reservation reservation = new Reservation(reservationHolder, book, LocalDateTime.now().minusMinutes(1));
        reservation.markReady(copy, LocalDateTime.now().plusHours(47));
        reservationRepository.save(reservation);
        BorrowRequest request = requestRepository.save(new BorrowRequest(
                requestingMember, book, LocalDateTime.now(), null));
        TestSession librarian = signIn("admin", "admin123");

        mockMvc.perform(post("/circulation/requests/{id}/approve", request.getId())
                        .session(librarian.session())
                        .param("_csrf", librarian.csrf()))
                .andExpect(status().is3xxRedirection());

        assertThat(requestRepository.findById(request.getId()).orElseThrow().getStatus())
                .isEqualTo(BorrowRequestStatus.PENDING);
        assertThat(reservationRepository.findById(reservation.getId()).orElseThrow().getStatus())
                .isEqualTo(ReservationStatus.READY);
        assertThat(copyRepository.findById(copy.getId()).orElseThrow().getStatus())
                .isEqualTo(CopyStatus.RESERVED);
        assertThat(loanRepository.countByMemberIdAndReturnedAtIsNull(requestingMember.getId())).isZero();
    }

    @Test
    void returnRollsBackLoanAndCopyWhenReservationQueueDispatchFails() {
        UserAccount member = createMember("rollback-reader");
        Book book = createBook("Return Rollback Check");
        BookCopy copy = new BookCopy("COPY-" + suffix(), book);
        copy.setStatus(CopyStatus.ON_LOAN);
        copy = copyRepository.save(copy);
        Loan loan = loanRepository.save(new Loan(member, copy, null,
                LocalDateTime.now().minusDays(3), LocalDate.now().minusDays(1)));

        doAnswer(invocation -> {
                    entityManager.flush();
                    throw new IllegalStateException("Simulated reservation dispatch failure");
                })
                .when(reservationRepository)
                .findWaitingForUpdate(book.getId(), ReservationStatus.WAITING);

        assertThatThrownBy(() -> circulationService.returnLoan(loan.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Simulated reservation dispatch failure");

        Loan persistedLoan = loanRepository.findById(loan.getId()).orElseThrow();
        assertThat(persistedLoan.getReturnedAt()).isNull();
        assertThat(persistedLoan.getFineAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(copyRepository.findById(copy.getId()).orElseThrow().getStatus())
                .isEqualTo(CopyStatus.ON_LOAN);
    }

    @Test
    void librarianUpdatesMemberSettingsWithoutChangingTheAccountRole() throws Exception {
        UserAccount member = createMember("managed-reader");
        TestSession librarian = signIn("admin", "admin123");

        mockMvc.perform(get("/members").session(librarian.session()))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(member.getUsername())));
        mockMvc.perform(post("/members/{id}/update", member.getId())
                        .session(librarian.session())
                        .param("_csrf", librarian.csrf())
                        .param("displayName", "Updated Course Member")
                        .param("membershipType", MembershipType.STUDENT.name())
                        .param("enabled", "true"))
                .andExpect(status().is3xxRedirection());

        UserAccount updated = userRepository.findById(member.getId()).orElseThrow();
        assertThat(updated.getDisplayName()).isEqualTo("Updated Course Member");
        assertThat(updated.getMembershipType()).isEqualTo(MembershipType.STUDENT);
        assertThat(updated.getRole()).isEqualTo(Role.MEMBER);

        UserAccount admin = userRepository.findByUsername("admin").orElseThrow();
        mockMvc.perform(post("/members/{id}/update", admin.getId())
                        .session(librarian.session())
                        .param("_csrf", librarian.csrf())
                        .param("displayName", "Attempted role change")
                        .param("membershipType", MembershipType.PREMIUM.name())
                        .param("enabled", "true"))
                .andExpect(status().is3xxRedirection());
        assertThat(userRepository.findById(admin.getId()).orElseThrow().getRole())
                .isEqualTo(Role.LIBRARIAN);
    }

    @Test
    void memberCancelsReadyReservationAndCopyReturnsToCatalogue() throws Exception {
        UserAccount member = createMember("reservation-reader");
        Book book = createBook("Cancellation Journey");
        BookCopy copy = new BookCopy("COPY-" + suffix(), book);
        copy.setStatus(CopyStatus.RESERVED);
        copyRepository.save(copy);
        Reservation reservation = new Reservation(member, book, LocalDateTime.now().minusHours(1));
        reservation.markReady(copy, LocalDateTime.now().plusHours(47));
        reservationRepository.save(reservation);
        TestSession memberSession = signIn(member.getUsername(), TEST_PASSWORD);

        mockMvc.perform(get("/circulation/mine").session(memberSession.session()))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Cancellation Journey")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Ready for collection")));
        mockMvc.perform(post("/reservations/{id}/cancel", reservation.getId())
                        .session(memberSession.session())
                        .param("_csrf", memberSession.csrf()))
                .andExpect(status().is3xxRedirection());

        assertThat(reservationRepository.findById(reservation.getId()).orElseThrow().getStatus())
                .isEqualTo(ReservationStatus.CANCELLED);
        assertThat(copyRepository.findById(copy.getId()).orElseThrow().getStatus())
                .isEqualTo(CopyStatus.AVAILABLE);
        mockMvc.perform(get("/circulation/mine").session(memberSession.session()))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Cancelled")));
    }

    @Test
    void librarianCanViewTitlesRankedByBorrowingFrequency() throws Exception {
        UserAccount member = createMember("report-reader");
        Book popularBook = createBook("Popular Course Title");
        BookCopy firstCopy = copyRepository.save(new BookCopy("COPY-" + suffix(), popularBook));
        BookCopy secondCopy = copyRepository.save(new BookCopy("COPY-" + suffix(), popularBook));
        Book lessPopularBook = createBook("Less Popular Course Title");
        BookCopy thirdCopy = copyRepository.save(new BookCopy("COPY-" + suffix(), lessPopularBook));
        LocalDateTime issuedAt = LocalDateTime.now().minusDays(3);
        LocalDate dueDate = LocalDate.now().plusDays(11);
        Loan returnedLoan = new Loan(member, firstCopy, null, issuedAt, dueDate);
        returnedLoan.markReturned(issuedAt.plusDays(1), BigDecimal.ZERO);
        loanRepository.save(returnedLoan);
        loanRepository.save(new Loan(member, secondCopy, null, issuedAt.plusMinutes(1), dueDate));
        loanRepository.save(new Loan(member, thirdCopy, null, issuedAt.plusMinutes(2), dueDate));

        TestSession librarian = signIn("admin", "admin123");
        String page = mockMvc.perform(get("/circulation/loans").session(librarian.session()))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Most borrowed titles")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Popular Course Title")))
                .andReturn().getResponse().getContentAsString();

        assertThat(page.indexOf("Popular Course Title"))
                .isLessThan(page.indexOf("Less Popular Course Title"));
        assertThat(page).contains("<td>2</td>", "<td>1</td>");
    }

    private UserAccount createMember(String prefix) {
        String username = prefix + "-" + suffix();
        return userRepository.save(new UserAccount(username, username + "@example.test",
                passwordEncoder.encode(TEST_PASSWORD), "Course Test Member", Role.MEMBER));
    }

    private Book createBook(String title) {
        String suffix = suffix();
        return bookRepository.save(new Book(title + " " + suffix, "ISBN-" + suffix,
                "Project Author", "Coursework", "Integration-test title."));
    }

    private String suffix() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private TestSession signIn(String username, String password) throws Exception {
        MvcResult loginPage = mockMvc.perform(get("/login")).andExpect(status().isOk()).andReturn();
        String initialToken = csrfToken(loginPage);
        MockHttpSession session = (MockHttpSession) loginPage.getRequest().getSession(false);
        mockMvc.perform(post("/login").session(session)
                        .param("_csrf", initialToken)
                        .param("username", username)
                        .param("password", password))
                .andExpect(status().is3xxRedirection());
        MvcResult authenticatedPage = mockMvc.perform(get("/login").session(session))
                .andExpect(status().isOk()).andReturn();
        return new TestSession(session, csrfToken(authenticatedPage));
    }

    private String csrfToken(MvcResult page) throws Exception {
        Matcher matcher = Pattern.compile("name=\"_csrf\"[^>]*value=\"([^\"]+)\"")
                .matcher(page.getResponse().getContentAsString());
        if (!matcher.find()) throw new AssertionError("Form did not render a CSRF token.");
        return matcher.group(1);
    }

    private record TestSession(MockHttpSession session, String csrf) { }
}
