package com.metsmarko.lhvcms.customer.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record CreateOrUpdateCustomerDto(
    @NotNull(message = "First name must not be empty")
    @Length(min = 1, max = 255, message = "First name must be between 1 and 255 characters")
    String firstName,

    @NotNull(message = "Last name must not be empty")
    @Length(min = 1, max = 255, message = "Last name must be between 1 and 255 characters")
    String lastName,

    @NotNull(message = "Email must not be empty")
    @Email(message = "Email must have valid format")
    @Length(min = 1, max = 255, message = "Email must be at most 255 characters")
    String email
) {
}
