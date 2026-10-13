package com.society.management.repository;

import com.society.management.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByReceiptNo(String receiptNo);
    Optional<Payment> findByBillId(Long billId);
    List<Payment> findAllByOrderByPaidOnDesc();
}
