package com.metsmarko.lhvcms.controller;

import com.metsmarko.lhvcms.BaseIntegrationTest;
import com.metsmarko.lhvcms.customer.model.CreateOrUpdateCustomerDto;
import com.metsmarko.lhvcms.customer.model.CustomerDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CustomerControllerTest extends BaseIntegrationTest {

  private static final String CUSTOMERS_ENDPOINT = "/customers";
  private static final String CUSTOMERS_ID_ENDPOINT = CUSTOMERS_ENDPOINT + "/{id}";

  @Test
  void testManageCustomers() {
    // create customer1
    CreateOrUpdateCustomerDto createCustomer1 = new CreateOrUpdateCustomerDto("firstName1", "lastName1", "email1@email.com");
    CustomerDto customer1Dto = givenHelper(createCustomer1)
        .when()
        .post(CUSTOMERS_ENDPOINT)
        .then()
        .statusCode(HttpStatus.OK.value())
        .extract()
        .body()
        .as(CustomerDto.class);
    assertNotNull(customer1Dto.id());
    assertEquals(createCustomer1.firstName(), customer1Dto.firstName());
    assertEquals(createCustomer1.lastName(), customer1Dto.lastName());
    assertEquals(createCustomer1.email(), customer1Dto.email());
    assertNotNull(customer1Dto.createdDtime());
    assertEquals(customer1Dto.createdDtime(), customer1Dto.modifiedDtime());
    assertEquals(customer1Dto, getCustomerById(customer1Dto.id()));

    // create customer2
    CreateOrUpdateCustomerDto createCustomer2 = new CreateOrUpdateCustomerDto("firstName2", "lastName2", "email2@email.com");
    CustomerDto customer2Dto = givenHelper(createCustomer2)
        .when()
        .post(CUSTOMERS_ENDPOINT)
        .then()
        .statusCode(HttpStatus.OK.value())
        .extract()
        .body()
        .as(CustomerDto.class);
    assertNotNull(customer2Dto.id());
    assertEquals(createCustomer2.firstName(), customer2Dto.firstName());
    assertEquals(createCustomer2.lastName(), customer2Dto.lastName());
    assertEquals(createCustomer2.email(), customer2Dto.email());
    assertNotNull(customer2Dto.createdDtime());
    assertEquals(customer2Dto.createdDtime(), customer2Dto.modifiedDtime());

    assertEquals(customer2Dto, getCustomerById(customer2Dto.id()));

    // update customer2
    CreateOrUpdateCustomerDto updateCustomer2 = new CreateOrUpdateCustomerDto(
        "firstName22", "lastName22", "email22@email.com"
    );
    CustomerDto updatedCustomer2Dto = givenHelper(updateCustomer2)
        .when()
        .put(CUSTOMERS_ID_ENDPOINT, customer2Dto.id())
        .then()
        .statusCode(HttpStatus.OK.value())
        .extract()
        .body()
        .as(CustomerDto.class);
    assertNotNull(customer2Dto.id());
    assertEquals(updateCustomer2.firstName(), updatedCustomer2Dto.firstName());
    assertEquals(updateCustomer2.lastName(), updatedCustomer2Dto.lastName());
    assertEquals(updateCustomer2.email(), updatedCustomer2Dto.email());
    assertEquals(customer2Dto.createdDtime(), updatedCustomer2Dto.createdDtime());
    assertTrue(updatedCustomer2Dto.modifiedDtime().isAfter(customer2Dto.modifiedDtime()));

    assertEquals(updatedCustomer2Dto, getCustomerById(customer2Dto.id()));

    // delete customer2
    givenHelper()
        .when()
        .delete(CUSTOMERS_ID_ENDPOINT, customer2Dto.id())
        .then()
        .statusCode(HttpStatus.OK.value());

    givenHelper()
        .get(CUSTOMERS_ID_ENDPOINT, customer2Dto.id())
        .then()
        .statusCode(HttpStatus.NOT_FOUND.value());

    // customer1 was not modified in any way during updates and deletes on customer2
    assertEquals(customer1Dto, getCustomerById(customer1Dto.id()));
  }

  @Test
  void testGetCustomer_DoesNotExist() {
    givenHelper()
        .get(CUSTOMERS_ID_ENDPOINT, UUID.randomUUID())
        .then()
        .statusCode(HttpStatus.NOT_FOUND.value());
  }

  @Test
  void testUpdateCustomer_DoesNotExist() {
    CreateOrUpdateCustomerDto updateCustomer = new CreateOrUpdateCustomerDto(
        "firstName", "lastName", "email@email.com"
    );
    givenHelper(updateCustomer)
        .when()
        .put(CUSTOMERS_ID_ENDPOINT, UUID.randomUUID())
        .then()
        .statusCode(HttpStatus.NOT_FOUND.value());
  }

  @Test
  void testCreateCustomer_BadInput() {
    CreateOrUpdateCustomerDto createCustomer = new CreateOrUpdateCustomerDto(
        "", "lastName", "email@email.com"
    );
    ProblemDetail problemDetail = givenHelper(createCustomer)
        .when()
        .post(CUSTOMERS_ENDPOINT)
        .then()
        .statusCode(HttpStatus.BAD_REQUEST.value())
        .extract()
        .body()
        .as(ProblemDetail.class);
    assertEquals("First name must be between 1 and 255 characters", problemDetail.getDetail());
  }

  @Test
  void testUpdateCustomer_BadInput() {
    CreateOrUpdateCustomerDto updateCustomer = new CreateOrUpdateCustomerDto(
        "firstName", null, "email@email.com"
    );
    ProblemDetail problemDetail = givenHelper(updateCustomer)
        .when()
        .put(CUSTOMERS_ID_ENDPOINT, UUID.randomUUID())
        .then()
        .statusCode(HttpStatus.BAD_REQUEST.value())
        .extract()
        .body()
        .as(ProblemDetail.class);
    assertEquals("Last name must not be empty", problemDetail.getDetail());
  }

  private CustomerDto getCustomerById(UUID id) {
    return givenHelper()
        .get(CUSTOMERS_ID_ENDPOINT, id)
        .then()
        .statusCode(HttpStatus.OK.value())
        .extract()
        .response()
        .body()
        .as(CustomerDto.class);
  }
}
