package com.society.management.service;

import com.society.management.dto.ResidentDto;
import com.society.management.model.Flat;
import com.society.management.model.Resident;
import com.society.management.model.Role;
import com.society.management.model.User;
import com.society.management.repository.FlatRepository;
import com.society.management.repository.ResidentRepository;
import com.society.management.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ResidentService {

    private final ResidentRepository residentRepository;
    private final UserRepository userRepository;
    private final FlatRepository flatRepository;
    private final PasswordEncoder passwordEncoder;

    public ResidentService(ResidentRepository residentRepository,
                           UserRepository userRepository,
                           FlatRepository flatRepository,
                           PasswordEncoder passwordEncoder) {
        this.residentRepository = residentRepository;
        this.userRepository = userRepository;
        this.flatRepository = flatRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Resident> getAllResidents() {
        return residentRepository.findAllByOrderByIdDesc();
    }

    public Optional<Resident> getResidentById(Long id) {
        return residentRepository.findById(id);
    }

    public Optional<Resident> getResidentByEmail(String email) {
        return residentRepository.findByUserEmail(email);
    }

    public long getTotalResidentsCount() {
        return residentRepository.count();
    }

    @Transactional
    public Resident createResident(ResidentDto dto) {
        String email = dto.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("User with email '" + email + "' already exists!");
        }

        Flat flat = flatRepository.findById(dto.getFlatId())
                .orElseThrow(() -> new IllegalArgumentException("Selected flat does not exist"));

        String rawPassword = (dto.getPassword() != null && !dto.getPassword().isBlank()) 
                ? dto.getPassword() : "resident123";

        User user = new User(
                dto.getName().trim(),
                email,
                passwordEncoder.encode(rawPassword),
                Role.ROLE_RESIDENT
        );
        user = userRepository.save(user);

        Resident resident = new Resident(
                user,
                flat,
                dto.getPhone().trim(),
                dto.getResidentType().trim()
        );

        return residentRepository.save(resident);
    }

    @Transactional
    public Resident updateResident(Long id, ResidentDto dto) {
        Resident resident = residentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Resident not found with id: " + id));

        User user = resident.getUser();
        String newEmail = dto.getEmail().trim().toLowerCase();

        if (!user.getEmail().equalsIgnoreCase(newEmail) && userRepository.existsByEmail(newEmail)) {
            throw new IllegalArgumentException("Email '" + newEmail + "' is already in use by another user!");
        }

        Flat flat = flatRepository.findById(dto.getFlatId())
                .orElseThrow(() -> new IllegalArgumentException("Selected flat does not exist"));

        user.setName(dto.getName().trim());
        user.setEmail(newEmail);
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        userRepository.save(user);

        resident.setFlat(flat);
        resident.setPhone(dto.getPhone().trim());
        resident.setResidentType(dto.getResidentType().trim());

        return residentRepository.save(resident);
    }

    @Transactional
    public void deleteResident(Long id) {
        Resident resident = residentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Resident not found with id: " + id));
        User user = resident.getUser();
        residentRepository.delete(resident);
        if (user != null) {
            userRepository.delete(user);
        }
    }
}
