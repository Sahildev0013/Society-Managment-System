package com.society.management.service;

import com.society.management.dto.VisitorDto;
import com.society.management.model.Flat;
import com.society.management.model.Visitor;
import com.society.management.repository.FlatRepository;
import com.society.management.repository.VisitorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class VisitorService {

    private final VisitorRepository visitorRepository;
    private final FlatRepository flatRepository;

    public VisitorService(VisitorRepository visitorRepository, FlatRepository flatRepository) {
        this.visitorRepository = visitorRepository;
        this.flatRepository = flatRepository;
    }

    public List<Visitor> getAllVisitors() {
        return visitorRepository.findAllByOrderByInTimeDesc();
    }

    public List<Visitor> getTodayVisitors() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
        return visitorRepository.findTodayVisitors(startOfDay, endOfDay);
    }

    public List<Visitor> getVisitorsForFlat(Long flatId) {
        return visitorRepository.findByFlatIdOrderByInTimeDesc(flatId);
    }

    public Optional<Visitor> getVisitorById(Long id) {
        return visitorRepository.findById(id);
    }

    @Transactional
    public Visitor logEntry(VisitorDto dto) {
        Flat flat = flatRepository.findById(dto.getFlatId())
                .orElseThrow(() -> new IllegalArgumentException("Selected flat does not exist"));

        Visitor visitor = new Visitor(
                flat,
                dto.getName().trim(),
                dto.getPhone().trim(),
                dto.getPurpose().trim(),
                LocalDateTime.now(),
                "PENDING" // pending resident approval
        );

        return visitorRepository.save(visitor);
    }

    @Transactional
    public Visitor markExit(Long visitorId) {
        Visitor visitor = visitorRepository.findById(visitorId)
                .orElseThrow(() -> new IllegalArgumentException("Visitor not found with id: " + visitorId));

        if (visitor.getOutTime() != null) {
            throw new IllegalStateException("Visitor exit has already been logged!");
        }

        visitor.setOutTime(LocalDateTime.now());
        visitor.setStatus("COMPLETED");
        return visitorRepository.save(visitor);
    }

    @Transactional
    public Visitor updateApprovalStatus(Long visitorId, String status) {
        Visitor visitor = visitorRepository.findById(visitorId)
                .orElseThrow(() -> new IllegalArgumentException("Visitor not found with id: " + visitorId));

        String upper = status.trim().toUpperCase();
        if (!upper.equals("APPROVED") && !upper.equals("REJECTED")) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }

        visitor.setStatus(upper);
        return visitorRepository.save(visitor);
    }
}
