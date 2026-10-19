package com.elibrary.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.LocalDateTime;

@Entity
@Table(name = "borrow_requests")
public class BorrowRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private UserAccount member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(nullable = false)
    private LocalDateTime requestedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BorrowRequestStatus status = BorrowRequestStatus.PENDING;

    @Column(length = 500)
    private String memberNote;

    @Column(length = 500)
    private String decisionNote;

    @Version
    private Long version;

    protected BorrowRequest() {
    }

    public BorrowRequest(UserAccount member, Book book, LocalDateTime requestedAt, String memberNote) {
        this.member = member;
        this.book = book;
        this.requestedAt = requestedAt;
        this.memberNote = memberNote;
    }

    public Long getId() { return id; }
    public UserAccount getMember() { return member; }
    public Book getBook() { return book; }
    public LocalDateTime getRequestedAt() { return requestedAt; }
    public BorrowRequestStatus getStatus() { return status; }
    public String getMemberNote() { return memberNote; }
    public String getDecisionNote() { return decisionNote; }
    public Long getVersion() { return version; }

    public void approve() {
        this.status = BorrowRequestStatus.APPROVED;
        this.decisionNote = null;
    }

    public void reject(String decisionNote) {
        this.status = BorrowRequestStatus.REJECTED;
        this.decisionNote = decisionNote;
    }
}
