package com.society.management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PaymentDto {

    @NotNull(message = "Bill ID is required")
    private Long billId;

    @NotBlank(message = "Payment mode must be selected")
    private String mode; // UPI, CREDIT_CARD, DEBIT_CARD, NET_BANKING, CASH

    public PaymentDto() {
    }

    public PaymentDto(Long billId, String mode) {
        this.billId = billId;
        this.mode = mode;
    }

    public Long getBillId() {
        return billId;
    }

    public void setBillId(Long billId) {
        this.billId = billId;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
