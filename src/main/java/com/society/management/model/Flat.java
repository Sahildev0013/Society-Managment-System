package com.society.management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "flats", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"wing", "flatNumber"})
})
public class Flat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Flat number is required")
    @Column(nullable = false)
    private String flatNumber;

    @NotBlank(message = "Wing is required")
    @Column(nullable = false)
    private String wing;

    @NotNull(message = "Floor is required")
    @Min(value = 0, message = "Floor must be 0 or higher")
    @Column(nullable = false)
    private Integer floor;

    @NotBlank(message = "Flat type is required")
    @Column(nullable = false)
    private String type; // e.g., 1 BHK, 2 BHK, 3 BHK, Penthouse

    @OneToMany(mappedBy = "flat", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Resident> residents = new ArrayList<>();

    @OneToMany(mappedBy = "flat", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MaintenanceBill> bills = new ArrayList<>();

    public Flat() {
    }

    public Flat(String flatNumber, String wing, Integer floor, String type) {
        this.flatNumber = flatNumber;
        this.wing = wing;
        this.floor = floor;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFlatNumber() {
        return flatNumber;
    }

    public void setFlatNumber(String flatNumber) {
        this.flatNumber = flatNumber;
    }

    public String getWing() {
        return wing;
    }

    public void setWing(String wing) {
        this.wing = wing;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Resident> getResidents() {
        return residents;
    }

    public void setResidents(List<Resident> residents) {
        this.residents = residents;
    }

    public List<MaintenanceBill> getBills() {
        return bills;
    }

    public void setBills(List<MaintenanceBill> bills) {
        this.bills = bills;
    }

    public String getFullFlatName() {
        return wing + "-" + flatNumber + " (" + type + ")";
    }

    public String getShortName() {
        return wing + "-" + flatNumber;
    }
}
