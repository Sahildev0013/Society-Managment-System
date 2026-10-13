package com.society.management.repository;

import com.society.management.model.Flat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FlatRepository extends JpaRepository<Flat, Long> {
    Optional<Flat> findByWingAndFlatNumber(String wing, String flatNumber);
    boolean existsByWingAndFlatNumber(String wing, String flatNumber);
    List<Flat> findAllByOrderByWingAscFlatNumberAsc();
}
