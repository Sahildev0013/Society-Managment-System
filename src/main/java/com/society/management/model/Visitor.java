package com.society.management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "visitors")
public class Visitor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Target flat is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "flat_id", nullable = false)
    private Flat flat;

    @NotBlank(message = "Visitor name is required")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Visitor phone number is required")
    @Column(nullable = false)
    private String phone;

    @NotBlank(message = "Purpose of visit is required")
    @Column(nullable = false)
    private String purpose;

    @NotNull(message = "Entry time is required")
    @Column(nullable = false)
    private LocalDateTime inTime;

    @Column
    private LocalDateTime outTime;

    @NotBlank(message = "Status is required")
    @Column(nullable = false)
    private String status; // "PENDING", "APPROVED", "REJECTED", "COMPLETED"

    public Visitor() {
    }

    public Visitor(Flat flat, String name, String phone, String purpose, LocalDateTime inTime, String status) {
        this.flat = flat;
        this.name = name;
        this.phone = phone;
        this.purpose = purpose;
        this.inTime = inTime;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Flat getFlat() {
        return flat;
    }

    public void setFlat(Flat flat) {
        this.flat = flat;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public LocalDateTime getInTime() {
        return inTime;
    }

    public void setInTime(LocalDateTime inTime) {
        this.inTime = inTime;
    }

    public LocalDateTime getOutTime() {
        return outTime;
    }

    public void setOutTime(LocalDateTime outTime) {
        this.outTime = outTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
