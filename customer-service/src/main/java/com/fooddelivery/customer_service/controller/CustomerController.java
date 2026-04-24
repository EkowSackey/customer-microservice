package com.fooddelivery.customer_service.controller;

import com.fooddelivery.customer_service.dto.*;
import com.fooddelivery.customer_service.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/me")
    public ResponseEntity<CustomerResponse> getMyProfile(Authentication auth) {
        return ResponseEntity.ok(customerService.getProfile(auth.getName()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getById(id));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<CustomerResponse> getByUsername(@PathVariable String username) {
        return ResponseEntity.ok(customerService.getProfile(username));
    }

    @PutMapping("/me")
    public ResponseEntity<CustomerResponse> updateProfile(
            Authentication auth, @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(customerService.updateProfile(auth.getName(), request));
    }
}

