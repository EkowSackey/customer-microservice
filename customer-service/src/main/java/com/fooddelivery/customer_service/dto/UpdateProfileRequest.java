package com.fooddelivery.customer_service.dto;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String firstName;
    private String lastName;
    private String phone;
    private String deliveryAddress;
    private String city;
}
