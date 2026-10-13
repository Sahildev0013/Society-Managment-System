package com.society.management.repository;

import com.society.management.model.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {
    List<Notice> findAllByOrderByCreatedOnDesc();
    List<Notice> findTop5ByOrderByCreatedOnDesc();
}
