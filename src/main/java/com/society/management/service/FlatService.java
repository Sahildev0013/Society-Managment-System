package com.society.management.service;

import com.society.management.dto.FlatDto;
import com.society.management.model.Flat;
import com.society.management.repository.FlatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class FlatService {

    private final FlatRepository flatRepository;

    public FlatService(FlatRepository flatRepository) {
        this.flatRepository = flatRepository;
    }

    public List<Flat> getAllFlats() {
        return flatRepository.findAllByOrderByWingAscFlatNumberAsc();
    }

    public Optional<Flat> getFlatById(Long id) {
        return flatRepository.findById(id);
    }

    public long getTotalFlatsCount() {
        return flatRepository.count();
    }

    @Transactional
    public Flat createFlat(FlatDto dto) {
        String wing = dto.getWing().trim().toUpperCase();
        String flatNumber = dto.getFlatNumber().trim();

        if (flatRepository.existsByWingAndFlatNumber(wing, flatNumber)) {
            throw new IllegalArgumentException("Flat " + wing + "-" + flatNumber + " already exists!");
        }

        Flat flat = new Flat(flatNumber, wing, dto.getFloor(), dto.getType());
        return flatRepository.save(flat);
    }

    @Transactional
    public Flat updateFlat(Long id, FlatDto dto) {
        Flat flat = flatRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Flat not found with id: " + id));

        String wing = dto.getWing().trim().toUpperCase();
        String flatNumber = dto.getFlatNumber().trim();

        // Check if updating to an existing wing/flatNumber combination on a different flat
        Optional<Flat> existing = flatRepository.findByWingAndFlatNumber(wing, flatNumber);
        if (existing.isPresent() && !existing.get().getId().equals(id)) {
            throw new IllegalArgumentException("Flat " + wing + "-" + flatNumber + " already exists!");
        }

        flat.setWing(wing);
        flat.setFlatNumber(flatNumber);
        flat.setFloor(dto.getFloor());
        flat.setType(dto.getType());
        return flatRepository.save(flat);
    }

    @Transactional
    public void deleteFlat(Long id) {
        Flat flat = flatRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Flat not found with id: " + id));
        if (flat.getResidents() != null && !flat.getResidents().isEmpty()) {
            throw new IllegalStateException("Cannot delete flat because it currently has assigned residents!");
        }
        flatRepository.delete(flat);
    }
}
