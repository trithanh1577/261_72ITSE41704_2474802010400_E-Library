package com.elibrary.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private UserAccount member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "copy_id", nullable = false)
    private BookCopy copy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", unique = true)
    private BorrowRequest request;

    @Column(nullable = false)
    private LocalDateTime issuedAt;

    @Column(nullable = false)
    private LocalDate dueDate;

    private LocalDateTime returnedAt;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal fineAmount = BigDecimal.ZERO;

    @Version
    private Long version;

    protected Loan() {
    }

    public Loan(UserAccount member, BookCopy copy, BorrowRequest request,
                LocalDateTime issuedAt, LocalDate dueDate) {
        this.member = member;
        this.copy = copy;
        this.request = request;
        this.issuedAt = issuedAt;
        this.dueDate = dueDate;
    }

    public Long getId() { return id; }
    public UserAccount getMember() { return member; }
    public BookCopy getCopy() { return copy; }
    public BorrowRequest getRequest() { return request; }
    public LocalDateTime getIssuedAt() { return issuedAt; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDateTime getReturnedAt() { return returnedAt; }
    public BigDecimal getFineAmount() { return fineAmount; }
    public Long getVersion() { return version; }

    public boolean isActive() { return returnedAt == null; }

    public void markReturned(LocalDateTime returnedAt, BigDecimal fineAmount) {
        this.returnedAt = returnedAt;
        this.fineAmount = fineAmount;
    }
}
