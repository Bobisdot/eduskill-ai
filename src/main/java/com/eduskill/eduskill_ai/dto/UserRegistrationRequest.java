package com.eduskill.eduskill_ai.dto;

import com.eduskill.eduskill_ai.entity.Role;
import lombok.Data;

@Data
public class UserRegistrationRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Role role;
}