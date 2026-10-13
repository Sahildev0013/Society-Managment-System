package com.society.management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class VisitorDto {

    @NotNull(message = "Please select the flat")
    private Long flatId;

    @NotBlank(message = "Visitor name is required")
    private String name;

    @NotBlank(message = "Visitor phone is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone must be a 10-digit number")
    private String phone;

    @NotBlank(message = "Purpose of visit is required")
    private String purpose;

    public VisitorDto() {
    }

    public VisitorDto(Long flatId, String name, String phone, String purpose) {
        this.flatId = flatId;
        this.name = name;
        this.phone = phone;
        this.purpose = purpose;
    }

    public Long getFlatId() {
        return flatId;
    }

    public void setFlatId(Long flatId) {
        this.flatId = flatId;
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
}
