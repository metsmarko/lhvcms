package com.metsmarko.lhvcms.customer.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "customer")
public class CustomerEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id")
  private UUID id;
  @Column(name = "first_name")
  private String firstName;
  @Column(name = "last_name")
  private String lastName;
  @Column(name = "email")
  private String email;
  @CreationTimestamp(source = SourceType.DB)
  @Column(name = "created_dtime")
  private Instant createdDtime;
  @UpdateTimestamp(source = SourceType.DB)
  @Column(name = "modified_dtime")
  private Instant modifiedDtime;

  public CustomerEntity(
      UUID id,
      String firstName,
      String lastName,
      String email,
      Instant createdDtime,
      Instant modifiedDtime
  ) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.createdDtime = createdDtime;
    this.modifiedDtime = modifiedDtime;
  }

  protected CustomerEntity() {
  }

  public CustomerEntity(CreateOrUpdateCustomerDto newCustomerDto) {
    this.firstName = newCustomerDto.firstName();
    this.lastName = newCustomerDto.lastName();
    this.email = newCustomerDto.email();
  }

  public UUID id() {
    return id;
  }

  public String firstName() {
    return firstName;
  }

  public String lastName() {
    return lastName;
  }

  public String email() {
    return email;
  }

  public Instant createdDtime() {
    return createdDtime;
  }

  public Instant modifiedDtime() {
    return modifiedDtime;
  }

  public CustomerDto toDto() {
    return new CustomerDto(id, firstName, lastName, email, createdDtime, modifiedDtime);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof CustomerEntity that)) {
      return false;
    }
    return Objects.equals(id, that.id) &&
        firstName.equals(that.firstName) &&
        lastName.equals(that.lastName) &&
        email.equals(that.email) &&
        Objects.equals(createdDtime, that.createdDtime) &&
        Objects.equals(modifiedDtime, that.modifiedDtime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, firstName, lastName, email, createdDtime, modifiedDtime);
  }
}
