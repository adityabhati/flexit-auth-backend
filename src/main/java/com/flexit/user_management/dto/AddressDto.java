package com.flexit.user_management.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressDto {
    private Long id;
    private UserDto userId;
    @NotBlank(message = "House number should not be blank.")
    private String houseNumber;
    @NotBlank(message = "Street should not be blank.")
    private String street;
    private String landmark;
    @NotBlank(message = "City should not be blank.")
    private String city;
    @NotBlank(message = "State should not be blank.")
    private String state;
    @NotBlank(message = "Country should not be blank.")
    private String country;
    @NotBlank(message = "Pin should not be blank.")
    private String pin;

}