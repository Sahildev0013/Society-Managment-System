package com.society.management.repository;

import com.society.management.model.Complaint;
import com.society.management.model.Resident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findAllByOrderByCreatedOnDesc();
    List<Complaint> findByResidentOrderByCreatedOnDesc(Resident resident);
    List<Complaint> findByResidentIdOrderByCreatedOnDesc(Long residentId);
    
    long countByStatus(String status);

    @Query("SELECT c.status, COUNT(c) FROM Complaint c GROUP BY c.status")
    List<Object[]> countComplaintsGroupedByStatus();
}
