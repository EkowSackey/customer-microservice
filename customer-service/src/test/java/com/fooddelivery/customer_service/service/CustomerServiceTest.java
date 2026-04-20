package com.fooddelivery.customer_service.service;

import com.fooddelivery.customer_service.dto.*;
import com.fooddelivery.customer_service.exception.*;
import com.fooddelivery.customer_service.model.Customer;
import com.fooddelivery.customer_service.repository.CustomerRepository;
import com.fooddelivery.customer_service.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private CustomerService customerService;

    private RegisterRequest registerRequest;
    private AuthRequest authRequest;
    private Customer customer;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password");
        registerRequest.setFirstName("Test");
        registerRequest.setLastName("User");

        authRequest = new AuthRequest();
        authRequest.setUsername("testuser");
        authRequest.setPassword("password");

        customer = Customer.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .role(Customer.Role.CUSTOMER)
                .build();
    }

    // --- register() Tests ---

    @Test
    void register_Success() {
        when(customerRepository.existsByUsername(anyString())).thenReturn(false);
        when(customerRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("mockToken");

        AuthResponse response = customerService.register(registerRequest);

        assertNotNull(response);
        assertEquals("mockToken", response.getToken());
        assertEquals("testuser", response.getUsername());
        assertEquals("CUSTOMER", response.getRole());

        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void register_DuplicateUsername_ThrowsException() {
        when(customerRepository.existsByUsername(anyString())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> customerService.register(registerRequest));

        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void register_DuplicateEmail_ThrowsException() {
        when(customerRepository.existsByUsername(anyString())).thenReturn(false);
        when(customerRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> customerService.register(registerRequest));

        verify(customerRepository, never()).save(any(Customer.class));
    }

    // --- login() Tests ---

    @Test
    void login_Success() {
        when(customerRepository.findByUsername(anyString())).thenReturn(Optional.of(customer));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("mockToken");

        AuthResponse response = customerService.login(authRequest);

        assertNotNull(response);
        assertEquals("mockToken", response.getToken());
        assertEquals("testuser", response.getUsername());
    }

    @Test
    void login_UserNotFound_ThrowsException() {
        when(customerRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> customerService.login(authRequest));
    }

    @Test
    void login_InvalidCredentials_ThrowsException() {
        when(customerRepository.findByUsername(anyString())).thenReturn(Optional.of(customer));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> customerService.login(authRequest));
    }

    // --- getProfile() Tests ---

    @Test
    void getProfile_Success() {
        when(customerRepository.findByUsername("testuser")).thenReturn(Optional.of(customer));

        CustomerResponse response = customerService.getProfile("testuser");

        assertNotNull(response);
        assertEquals("testuser", response.getUsername());
    }

    @Test
    void getProfile_NotFound_ThrowsException() {
        when(customerRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> customerService.getProfile("unknown"));
    }

    // --- getById() Tests ---

    @Test
    void getById_Success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        CustomerResponse response = customerService.getById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getById_NotFound_ThrowsException() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> customerService.getById(99L));
    }

    // --- updateProfile() Tests ---

    @Test
    void updateProfile_Success() {
        when(customerRepository.findByUsername("testuser")).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        RegisterRequest updateRequest = new RegisterRequest();
        updateRequest.setFirstName("UpdatedName");

        CustomerResponse response = customerService.updateProfile("testuser", updateRequest);

        assertNotNull(response);
        verify(customerRepository).save(customer);
        assertEquals("UpdatedName", customer.getFirstName()); // verify the entity was modified before save
    }

    @Test
    void updateProfile_NotFound_ThrowsException() {
        when(customerRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> customerService.updateProfile("unknown", new RegisterRequest()));
        verify(customerRepository, never()).save(any(Customer.class));
    }
}
