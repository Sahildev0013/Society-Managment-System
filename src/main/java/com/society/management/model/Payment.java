package com.society.management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Bill reference is required")
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bill_id", nullable = false, unique = true)
    private MaintenanceBill bill;

    @NotNull(message = "Payment date is required")
    @Column(nullable = false)
    private LocalDateTime paidOn;

    @NotBlank(message = "Payment mode is required")
    @Column(nullable = false)
    private String mode; // "UPI", "CREDIT_CARD", "DEBIT_CARD", "NET_BANKING", "CASH"

    @NotBlank(message = "Receipt number is required")
    @Column(nullable = false, unique = true)
    private String receiptNo;

    @Column(name = "transaction_ref")
    private String transactionRef;

    public Payment() {
    }

    public Payment(MaintenanceBill bill, LocalDateTime paidOn, String mode, String receiptNo, String transactionRef) {
        this.bill = bill;
        this.paidOn = paidOn;
        this.mode = mode;
        this.receiptNo = receiptNo;
        this.transactionRef = transactionRef;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MaintenanceBill getBill() {
        return bill;
    }

    public void setBill(MaintenanceBill bill) {
        this.bill = bill;
    }

    public LocalDateTime getPaidOn() {
        return paidOn;
    }

    public void setPaidOn(LocalDateTime paidOn) {
        this.paidOn = paidOn;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getReceiptNo() {
        return receiptNo;
    }

    public void setReceiptNo(String receiptNo) {
        this.receiptNo = receiptNo;
    }

    public String getTransactionRef() {
        return transactionRef;
    }

    public void setTransactionRef(String transactionRef) {
        this.transactionRef = transactionRef;
    }
}
