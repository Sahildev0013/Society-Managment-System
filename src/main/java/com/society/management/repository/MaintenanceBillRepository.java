package com.society.management.repository;

import com.society.management.model.Flat;
import com.society.management.model.MaintenanceBill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaintenanceBillRepository extends JpaRepository<MaintenanceBill, Long> {
    
    boolean existsByFlatAndBillingMonth(Flat flat, String billingMonth);
    
    Optional<MaintenanceBill> findByFlatAndBillingMonth(Flat flat, String billingMonth);
    
    List<MaintenanceBill> findByFlatOrderByBillingMonthDesc(Flat flat);
    
    List<MaintenanceBill> findByFlatIdOrderByBillingMonthDesc(Long flatId);
    
    List<MaintenanceBill> findAllByOrderByBillingMonthDesc();
    
    List<MaintenanceBill> findByBillingMonth(String billingMonth);
    
    List<MaintenanceBill> findByStatus(String status);
    
    List<MaintenanceBill> findByBillingMonthAndStatus(String billingMonth, String status);
    
    long countByStatus(String status);

    @Query("SELECT DISTINCT b.billingMonth FROM MaintenanceBill b ORDER BY b.billingMonth DESC")
    List<String> findDistinctBillingMonths();

    @Query("SELECT b.billingMonth, SUM(b.amount) FROM MaintenanceBill b WHERE b.status = 'PAID' GROUP BY b.billingMonth ORDER BY b.billingMonth ASC")
    List<Object[]> findMonthlyCollectionStats();

    @Query("SELECT SUM(b.amount) FROM MaintenanceBill b WHERE b.status = 'PAID'")
    Double getTotalPaidAmount();

    @Query("SELECT SUM(b.amount) FROM MaintenanceBill b WHERE b.status = 'PENDING'")
    Double getTotalPendingAmount();

    @Query("SELECT COUNT(b) FROM MaintenanceBill b WHERE b.flat.id = :flatId AND b.status = 'PENDING'")
    long countPendingBillsByFlatId(@Param("flatId") Long flatId);
}
