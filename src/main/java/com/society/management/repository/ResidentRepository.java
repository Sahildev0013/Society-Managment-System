package com.society.management.repository;

import com.society.management.model.Flat;
import com.society.management.model.Resident;
import com.society.management.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResidentRepository extends JpaRepository<Resident, Long> {
    Optional<Resident> findByUser(User user);
    Optional<Resident> findByUserEmail(String email);
    List<Resident> findByFlat(Flat flat);
    boolean existsByUserEmail(String email);
    List<Resident> findAllByOrderByIdDesc();
}
