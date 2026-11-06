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
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "fine_settlements")
public class FineSettlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FineSettlementType type;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 500)
    private String note;

    @Column(nullable = false, length = 80)
    private String processedBy;

    @Column(nullable = false)
    private LocalDateTime recordedAt;

    protected FineSettlement() { }

    public FineSettlement(Loan loan, FineSettlementType type, BigDecimal amount,
                          String note, String processedBy, LocalDateTime recordedAt) {
        this.loan = loan;
        this.type = type;
        this.amount = amount;
        this.note = note;
        this.processedBy = processedBy;
        this.recordedAt = recordedAt;
    }

    public Long getId() { return id; }
    public Loan getLoan() { return loan; }
    public FineSettlementType getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public String getNote() { return note; }
    public String getProcessedBy() { return processedBy; }
    public LocalDateTime getRecordedAt() { return recordedAt; }
}
