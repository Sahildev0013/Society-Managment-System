package com.society.management.repository;

import com.society.management.model.Flat;
import com.society.management.model.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VisitorRepository extends JpaRepository<Visitor, Long> {
    List<Visitor> findAllByOrderByInTimeDesc();
    List<Visitor> findByFlatOrderByInTimeDesc(Flat flat);
    List<Visitor> findByFlatIdOrderByInTimeDesc(Long flatId);
    
    @Query("SELECT v FROM Visitor v WHERE v.inTime >= :startOfDay AND v.inTime <= :endOfDay ORDER BY v.inTime DESC")
    List<Visitor> findTodayVisitors(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);
    
    long countByStatus(String status);
}
