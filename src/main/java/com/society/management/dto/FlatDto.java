package com.society.management.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class FlatDto {

    private Long id;

    @NotBlank(message = "Flat number is required (e.g. 101, 202)")
    private String flatNumber;

    @NotBlank(message = "Wing is required (e.g. A, B, C)")
    private String wing;

    @NotNull(message = "Floor is required")
    @Min(value = 0, message = "Floor must be 0 or positive")
    private Integer floor;

    @NotBlank(message = "Flat type is required (e.g. 1 BHK, 2 BHK, 3 BHK)")
    private String type;

    public FlatDto() {
    }

    public FlatDto(Long id, String flatNumber, String wing, Integer floor, String type) {
        this.id = id;
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
}
