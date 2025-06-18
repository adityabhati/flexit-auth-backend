package com.flexit.user_management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDto {
    @NotBlank(message = "First name should not be blank.")
    private String firstName;
    @NotBlank(message = "Last name should not be blank.")
    private String lastName;
    @NotNull(message = "Age should not be blank.")
    private int age;
    @NotBlank(message = "Email should not be blank.")
    private String email;
    @NotBlank(message = "Phone number should not be blank.")
    private String phoneNumber;
}