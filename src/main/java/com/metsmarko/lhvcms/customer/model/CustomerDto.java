package com.metsmarko.lhvcms.customer.model;

import java.time.Instant;
import java.util.UUID;

public record CustomerDto(
    UUID id,
    String firstName,
    String lastName,
    String email,
    Instant createdDtime,
    Instant modifiedDtime
) {
}
