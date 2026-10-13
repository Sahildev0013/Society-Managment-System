package com.society.management.service;

import com.society.management.dto.BillGenerationDto;
import com.society.management.model.Flat;
import com.society.management.model.MaintenanceBill;
import com.society.management.repository.FlatRepository;
import com.society.management.repository.MaintenanceBillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class MaintenanceBillService {

    private final MaintenanceBillRepository billRepository;
    private final FlatRepository flatRepository;

    public MaintenanceBillService(MaintenanceBillRepository billRepository, FlatRepository flatRepository) {
        this.billRepository = billRepository;
        this.flatRepository = flatRepository;
    }

    public List<MaintenanceBill> getAllBills() {
        return billRepository.findAllByOrderByBillingMonthDesc();
    }

    public Optional<MaintenanceBill> getBillById(Long id) {
        return billRepository.findById(id);
    }

    public List<MaintenanceBill> getBillsForFlat(Long flatId) {
        return billRepository.findByFlatIdOrderByBillingMonthDesc(flatId);
    }

    public List<String> getDistinctBillingMonths() {
        return billRepository.findDistinctBillingMonths();
    }

    public long getPendingBillsCount() {
        return billRepository.countByStatus("PENDING");
    }

    public Double getTotalCollection() {
        Double total = billRepository.getTotalPaidAmount();
        return total != null ? total : 0.0;
    }

    public Double getTotalPendingAmount() {
        Double pending = billRepository.getTotalPendingAmount();
        return pending != null ? pending : 0.0;
    }

    public Map<String, Double> getMonthlyCollectionMap() {
        List<Object[]> rows = billRepository.findMonthlyCollectionStats();
        Map<String, Double> result = new LinkedHashMap<>();
        for (Object[] row : rows) {
            String month = (String) row[0];
            Double sum = (Double) row[1];
            result.put(month, sum != null ? sum : 0.0);
        }
        return result;
    }

    public List<MaintenanceBill> filterBills(String month, String status) {
        boolean hasMonth = (month != null && !month.trim().isEmpty() && !month.equalsIgnoreCase("ALL"));
        boolean hasStatus = (status != null && !status.trim().isEmpty() && !status.equalsIgnoreCase("ALL"));

        if (hasMonth && hasStatus) {
            return billRepository.findByBillingMonthAndStatus(month.trim(), status.trim().toUpperCase());
        } else if (hasMonth) {
            return billRepository.findByBillingMonth(month.trim());
        } else if (hasStatus) {
            return billRepository.findByStatus(status.trim().toUpperCase());
        } else {
            return billRepository.findAllByOrderByBillingMonthDesc();
        }
    }

    /**
     * Generates monthly maintenance bills for all flats at once.
     * Prevents duplicate bills for the same flat and month.
     * Returns a summary message with generated and skipped counts.
     */
    @Transactional
    public String generateBillsForAllFlats(BillGenerationDto dto) {
        List<Flat> flats = flatRepository.findAll();
        if (flats.isEmpty()) {
            throw new IllegalStateException("No flats registered in the society yet. Please add flats first.");
        }

        int generatedCount = 0;
        int skippedCount = 0;

        String month = dto.getBillingMonth().trim();

        for (Flat flat : flats) {
            if (billRepository.existsByFlatAndBillingMonth(flat, month)) {
                skippedCount++;
                continue;
            }

            MaintenanceBill bill = new MaintenanceBill(
                    flat,
                    month,
                    dto.getAmount(),
                    dto.getDueDate(),
                    "PENDING"
            );
            billRepository.save(bill);
            generatedCount++;
        }

        if (generatedCount == 0 && skippedCount > 0) {
            throw new IllegalArgumentException("Bills for month " + month + " already exist for all " + skippedCount + " flats. No new bills created.");
        }

        return "Successfully generated " + generatedCount + " bill(s) for " + month + 
               (skippedCount > 0 ? " (" + skippedCount + " skipped as they already existed)" : "");
    }
}
