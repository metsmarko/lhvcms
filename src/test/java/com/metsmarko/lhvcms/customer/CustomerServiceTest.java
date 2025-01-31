package com.metsmarko.lhvcms.customer;

import com.metsmarko.lhvcms.customer.model.CreateOrUpdateCustomerDto;
import com.metsmarko.lhvcms.customer.model.CustomerDto;
import com.metsmarko.lhvcms.customer.model.CustomerEntity;
import com.metsmarko.lhvcms.exception.BadInputException;
import jakarta.validation.Validation;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CustomerServiceTest {

  private static final String VALID_LONG_EMAIL = "%s@%s.%s.%s.b".formatted(
      StringUtils.repeat("a", 64),
      StringUtils.repeat("b", 63),
      StringUtils.repeat("b", 63),
      StringUtils.repeat("b", 60)
  );
  private final CustomerRepository repository = mock(CustomerRepository.class);
  private final CustomerService service;
  private final CustomerEntity entity = new CustomerEntity(
      UUID.randomUUID(),
      "first",
      "last",
      "e@mail.com",
      Instant.now(),
      Instant.now()
  );
  private final Map<CreateOrUpdateCustomerDto, String> badInputCases = Map.of(
      new CreateOrUpdateCustomerDto(StringUtils.repeat("a", 256), "last", "e@mail.com"),
      "First name must be between 1 and 255 characters",
      new CreateOrUpdateCustomerDto("", "last", "e@mail.com"), "First name must be between 1 and 255 characters",
      new CreateOrUpdateCustomerDto(null, "last", "e@mail.com"), "First name must not be empty",

      new CreateOrUpdateCustomerDto("first", StringUtils.repeat("a", 256), "e@mail.com"),
      "Last name must be between 1 and 255 characters",
      new CreateOrUpdateCustomerDto("first", "", "e@mail.com"), "Last name must be between 1 and 255 characters",
      new CreateOrUpdateCustomerDto("first", null, "e@mail.com"), "Last name must not be empty",

      new CreateOrUpdateCustomerDto("first", "last", VALID_LONG_EMAIL + "b"), "Email must be at most 255 characters",
      new CreateOrUpdateCustomerDto("first", "last", ""), "Email must be at most 255 characters",
      new CreateOrUpdateCustomerDto("first", "last", null), "Email must not be empty",
      new CreateOrUpdateCustomerDto("first", "last", "dsada"), "Email must have valid format"
  );

  CustomerServiceTest() {
    try (var factory = Validation.buildDefaultValidatorFactory()) {
      service = new CustomerService(repository, factory.getValidator());
    }
  }

  @Test
  void testInsertCustomerOk() throws Exception {
    CreateOrUpdateCustomerDto newCustomer = new CreateOrUpdateCustomerDto(
        StringUtils.repeat("a", 255),
        StringUtils.repeat("b", 255),
        VALID_LONG_EMAIL
    );
    CustomerEntity newEntity = new CustomerEntity(newCustomer);
    Instant createdDtime = Instant.now();
    CustomerEntity savedEntity = new CustomerEntity(
        UUID.randomUUID(),
        newCustomer.firstName(),
        newCustomer.lastName(),
        newCustomer.email(),
        createdDtime,
        createdDtime
    );
    when(repository.save(newEntity)).thenReturn(savedEntity);

    CustomerDto customer = service.insertCustomer(newCustomer);

    verify(repository).save(newEntity);
    assertEquals(savedEntity.id(), customer.id());
    assertEquals(savedEntity.firstName(), customer.firstName());
    assertEquals(savedEntity.lastName(), customer.lastName());
    assertEquals(savedEntity.email(), customer.email());
    assertEquals(savedEntity.createdDtime(), customer.createdDtime());
    assertEquals(savedEntity.modifiedDtime(), customer.modifiedDtime());
  }

  @Test
  void testInsertCustomer_BadInput() {
    badInputCases.forEach(this::assertBadInputOnInsert);
  }

  @Test
  void testGetCustomerById() {
    when(repository.findById(entity.id())).thenReturn(Optional.of(entity));

    Optional<CustomerDto> customerById = service.getCustomerById(entity.id());

    assertTrue(customerById.isPresent());
    CustomerDto customer = customerById.get();
    assertEquals(entity.id(), customer.id());
    assertEquals(entity.firstName(), customer.firstName());
    assertEquals(entity.lastName(), customer.lastName());
    assertEquals(entity.email(), customer.email());
    assertEquals(entity.createdDtime(), customer.createdDtime());
    assertEquals(entity.modifiedDtime(), customer.modifiedDtime());
  }

  @Test
  void testUpdateCustomer() throws Exception {
    CreateOrUpdateCustomerDto updatedCustomerDto = new CreateOrUpdateCustomerDto("f", "l", "email@e.com");
    CustomerEntity updatedEntity = new CustomerEntity(
        entity.id(), updatedCustomerDto.firstName(), updatedCustomerDto.lastName(), updatedCustomerDto.email(),
        entity.createdDtime(), Instant.now()
    );
    when(repository.findById(entity.id())).thenReturn(Optional.of(entity));
    when(repository.save(any())).thenReturn(updatedEntity);

    Optional<CustomerDto> customerById = service.updateCustomer(
        entity.id(), updatedCustomerDto
    );

    verify(repository).save(argThat(ce -> {
      assertEquals(entity.id(), ce.id());
      assertEquals(updatedCustomerDto.firstName(), ce.firstName());
      assertEquals(updatedCustomerDto.lastName(), ce.lastName());
      assertEquals(updatedCustomerDto.email(), ce.email());
      assertEquals(entity.createdDtime(), ce.createdDtime());
      assertNotEquals(entity.modifiedDtime(), ce.modifiedDtime());
      assertTrue(ce.modifiedDtime().isAfter(entity.modifiedDtime()));
      return true;
    }));

    assertTrue(customerById.isPresent());
    CustomerDto customer = customerById.get();
    assertEquals(entity.id(), customer.id());
    assertEquals(updatedCustomerDto.firstName(), customer.firstName());
    assertEquals(updatedCustomerDto.lastName(), customer.lastName());
    assertEquals(updatedCustomerDto.email(), customer.email());
    assertEquals(entity.createdDtime(), customer.createdDtime());
  }

  @Test
  void testUpdateCustomer_BadInput() {
    badInputCases.forEach(this::assertBadInputOnUpdate);
  }

  @Test
  void testDeleteCustomer() {
    service.deleteCustomerById(entity.id());

    verify(repository).deleteById(entity.id());
  }

  private void assertBadInputOnInsert(CreateOrUpdateCustomerDto dto, String expectedError) {
    BadInputException ex = assertThrows(BadInputException.class, () -> service.insertCustomer(dto));
    assertEquals(expectedError, ex.getMessage());
  }

  private void assertBadInputOnUpdate(CreateOrUpdateCustomerDto dto, String expectedError) {
    BadInputException ex = assertThrows(BadInputException.class, () -> service.updateCustomer(UUID.randomUUID(), dto));
    assertEquals(expectedError, ex.getMessage());
  }
}
