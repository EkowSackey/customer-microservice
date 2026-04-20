package com.fooddelivery.customer_service.dto;

import com.fooddelivery.customer_service.model.Customer;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CustomerResponse {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String deliveryAddress;
    private String city;
    private String role;
    private LocalDateTime createdAt;

    public static CustomerResponse fromEntity(Customer c) {
        CustomerResponse dto = new CustomerResponse();
        dto.setId(c.getId());
        dto.setUsername(c.getUsername());
        dto.setEmail(c.getEmail());
        dto.setFirstName(c.getFirstName());
        dto.setLastName(c.getLastName());
        dto.setPhone(c.getPhone());
        dto.setDeliveryAddress(c.getDeliveryAddress());
        dto.setCity(c.getCity());
        dto.setRole(c.getRole().name());
        dto.setCreatedAt(c.getCreatedAt());

        return dto;
    }
}
