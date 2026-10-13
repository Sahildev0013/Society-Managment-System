package com.society.management.service;

import com.society.management.dto.PaymentDto;
import com.society.management.model.MaintenanceBill;
import com.society.management.model.Payment;
import com.society.management.repository.MaintenanceBillRepository;
import com.society.management.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final MaintenanceBillRepository billRepository;

    public PaymentService(PaymentRepository paymentRepository, MaintenanceBillRepository billRepository) {
        this.paymentRepository = paymentRepository;
        this.billRepository = billRepository;
    }

    public Optional<Payment> getPaymentByReceiptNo(String receiptNo) {
        return paymentRepository.findByReceiptNo(receiptNo);
    }

    public Optional<Payment> getPaymentByBillId(Long billId) {
        return paymentRepository.findByBillId(billId);
    }

    @Transactional
    public Payment processPayment(PaymentDto dto) {
        MaintenanceBill bill = billRepository.findById(dto.getBillId())
                .orElseThrow(() -> new IllegalArgumentException("Bill not found with ID: " + dto.getBillId()));

        if ("PAID".equalsIgnoreCase(bill.getStatus())) {
            throw new IllegalStateException("This bill has already been paid!");
        }

        LocalDateTime now = LocalDateTime.now();
        String datePrefix = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomSuffix = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        String receiptNo = "REC-" + datePrefix + "-" + randomSuffix;
        String transactionRef = "TXN-" + System.currentTimeMillis();

        bill.setStatus("PAID");
        billRepository.save(bill);

        Payment payment = new Payment(
                bill,
                now,
                dto.getMode().trim().toUpperCase(),
                receiptNo,
                transactionRef
        );

        return paymentRepository.save(payment);
    }
}
