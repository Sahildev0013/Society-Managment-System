package com.society.management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "complaints")
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Resident reference is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "resident_id", nullable = false)
    private Resident resident;

    @NotBlank(message = "Complaint category is required")
    @Column(nullable = false)
    private String category;

    @NotBlank(message = "Description is required")
    @Column(nullable = false, length = 4000)
    private String description;

    @NotBlank(message = "Status is required")
    @Column(nullable = false)
    private String status; // "OPEN", "IN_PROGRESS", "RESOLVED"

    @NotNull(message = "Created date is required")
    @Column(nullable = false)
    private LocalDateTime createdOn;

    @Column(length = 2000)
    private String adminRemark;

    public Complaint() {
    }

    public Complaint(Resident resident, String category, String description, String status, LocalDateTime createdOn) {
        this.resident = resident;
        this.category = category;
        this.description = description;
        this.status = status;
        this.createdOn = createdOn;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public String getAdminRemark() {
        return adminRemark;
    }

    public void setAdminRemark(String adminRemark) {
        this.adminRemark = adminRemark;
    }
}
