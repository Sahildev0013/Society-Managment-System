package com.society.management.service;

import com.society.management.dto.ComplaintDto;
import com.society.management.model.Complaint;
import com.society.management.model.Resident;
import com.society.management.repository.ComplaintRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ComplaintService {

    private final ComplaintRepository complaintRepository;

    public ComplaintService(ComplaintRepository complaintRepository) {
        this.complaintRepository = complaintRepository;
    }

    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAllByOrderByCreatedOnDesc();
    }

    public List<Complaint> getComplaintsByResident(Resident resident) {
        return complaintRepository.findByResidentOrderByCreatedOnDesc(resident);
    }

    public Optional<Complaint> getComplaintById(Long id) {
        return complaintRepository.findById(id);
    }

    public long getOpenComplaintsCount() {
        return complaintRepository.countByStatus("OPEN");
    }

    public Map<String, Long> getComplaintsStatusDistribution() {
        Map<String, Long> map = new LinkedHashMap<>();
        map.put("OPEN", complaintRepository.countByStatus("OPEN"));
        map.put("IN_PROGRESS", complaintRepository.countByStatus("IN_PROGRESS"));
        map.put("RESOLVED", complaintRepository.countByStatus("RESOLVED"));
        return map;
    }

    @Transactional
    public Complaint raiseComplaint(Resident resident, ComplaintDto dto) {
        Complaint complaint = new Complaint(
                resident,
                dto.getCategory().trim(),
                dto.getDescription().trim(),
                "OPEN",
                LocalDateTime.now()
        );
        return complaintRepository.save(complaint);
    }

    @Transactional
    public Complaint updateComplaintStatus(Long id, String status, String remark) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Complaint not found with id: " + id));

        String upper = status.trim().toUpperCase();
        if (!upper.equals("OPEN") && !upper.equals("IN_PROGRESS") && !upper.equals("RESOLVED")) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }

        complaint.setStatus(upper);
        if (remark != null) {
            complaint.setAdminRemark(remark.trim());
        }
        return complaintRepository.save(complaint);
    }
}
