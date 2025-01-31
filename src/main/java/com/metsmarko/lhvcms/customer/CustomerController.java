package com.metsmarko.lhvcms.customer;

import com.metsmarko.lhvcms.customer.model.CreateOrUpdateCustomerDto;
import com.metsmarko.lhvcms.customer.model.CustomerDto;
import com.metsmarko.lhvcms.exception.BadInputException;
import com.metsmarko.lhvcms.exception.NotFoundException;
import com.metsmarko.lhvcms.swagger.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("customers")
@ApiResponse(responseCode = "200", description = "Ok")
public class CustomerController {
  private final CustomerService service;

  @Autowired
  public CustomerController(CustomerService customerService) {
    this.service = Objects.requireNonNull(customerService);
  }

  @PostMapping
  @Operation(summary = "Creates new customer")
  @ApiErrorResponse(responseCode = "400", description = "Invalid input")
  public ResponseEntity<CustomerDto> insertCustomer(
      @RequestBody CreateOrUpdateCustomerDto newCustomerDto
  ) throws BadInputException {
    return ResponseEntity.ok(service.insertCustomer(newCustomerDto));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Gets customer by id")
  @ApiErrorResponse(responseCode = "404", description = "Customer not found")
  public ResponseEntity<CustomerDto> getCustomerById(@PathVariable UUID id) {
    return service.getCustomerById(id)
        .map(ResponseEntity::ok)
        .orElseThrow(() -> new NotFoundException("Customer not found"));
  }

  @PutMapping("/{id}")
  @Operation(summary = "Updates customer by id")
  @ApiErrorResponse(responseCode = "400", description = "Invalid input")
  @ApiErrorResponse(responseCode = "404", description = "Customer not found")
  public ResponseEntity<CustomerDto> updateCustomer(
      @PathVariable UUID id,
      @RequestBody CreateOrUpdateCustomerDto customerDto
  ) throws BadInputException {
    return service.updateCustomer(id, customerDto)
        .map(ResponseEntity::ok)
        .orElseThrow(() -> new NotFoundException("Customer not found"));
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Deletes customer by id")
  public void deleteCustomerById(@PathVariable UUID id) {
    service.deleteCustomerById(id);
  }
}
