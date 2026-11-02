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
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private UserAccount member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "copy_id")
    private BookCopy copy;

    @Column(nullable = false)
    private LocalDateTime reservedAt;

    private LocalDateTime readyUntil;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReservationStatus status = ReservationStatus.WAITING;

    @Version
    private Long version;

    protected Reservation() { }

    public Reservation(UserAccount member, Book book, LocalDateTime reservedAt) {
        this.member = member;
        this.book = book;
        this.reservedAt = reservedAt;
    }

    public Long getId() { return id; }
    public UserAccount getMember() { return member; }
    public Book getBook() { return book; }
    public BookCopy getCopy() { return copy; }
    public LocalDateTime getReservedAt() { return reservedAt; }
    public LocalDateTime getReadyUntil() { return readyUntil; }
    public ReservationStatus getStatus() { return status; }
    public Long getVersion() { return version; }

    public void markReady(BookCopy copy, LocalDateTime readyUntil) {
        this.copy = copy;
        this.readyUntil = readyUntil;
        this.status = ReservationStatus.READY;
    }

    public void fulfill() { this.status = ReservationStatus.FULFILLED; }
    public void cancel() { this.status = ReservationStatus.CANCELLED; }
    public void expire() { this.status = ReservationStatus.EXPIRED; }
    public void skip() { this.status = ReservationStatus.SKIPPED; }
}
