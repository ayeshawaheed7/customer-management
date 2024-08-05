package com.ayeshascode.customer.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public record CustomerRegistrationRequest(
        @NotBlank(message = "First name is mandatory")
        String firstName,

        @NotBlank(message = "Last name is mandatory")
        String lastName,

        @NotBlank(message = "Email is mandatory")
        @Email(message = "Email should be valid")
        String email
) {
}
