package com.metsmarko.lhvcms.customer;

import com.metsmarko.lhvcms.customer.model.CreateOrUpdateCustomerDto;
import com.metsmarko.lhvcms.customer.model.CustomerDto;
import com.metsmarko.lhvcms.customer.model.CustomerEntity;
import com.metsmarko.lhvcms.exception.BadInputException;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class CustomerService {
  private static final Logger log = LoggerFactory.getLogger(CustomerService.class);

  private final CustomerRepository repository;
  private final Validator validator;

  @Autowired
  public CustomerService(CustomerRepository repository, Validator validator) {
    this.repository = Objects.requireNonNull(repository);
    this.validator = Objects.requireNonNull(validator);
  }

  @Transactional
  public CustomerDto insertCustomer(CreateOrUpdateCustomerDto newCustomerDto) throws BadInputException {
    validateCustomerDto(newCustomerDto);
    CustomerEntity entity = repository.save(new CustomerEntity(newCustomerDto));
    repository.flush();
    return entity.toDto();
  }

  public Optional<CustomerDto> getCustomerById(UUID id) {
    return repository
        .findById(id)
        .map(CustomerEntity::toDto);
  }

  @Transactional
  public Optional<CustomerDto> updateCustomer(
      UUID id,
      CreateOrUpdateCustomerDto customerDto
  ) throws BadInputException {
    validateCustomerDto(customerDto);
    return repository
        .findById(id)
        .map(ce -> {
          var updatedCustomer = new CustomerEntity(
              ce.id(),
              customerDto.firstName(),
              customerDto.lastName(),
              customerDto.email(),
              ce.createdDtime(),
              Instant.now()
          );
          CustomerEntity entity = repository.save(updatedCustomer);
          repository.flush();
          return entity.toDto();
        });
  }

  @Transactional
  public void deleteCustomerById(UUID id) {
    repository.deleteById(id);
  }

  private void validateCustomerDto(CreateOrUpdateCustomerDto newCustomerDto) throws BadInputException {
    var res = validator.validate(newCustomerDto);
    if (!res.isEmpty()) {
      log.warn("Invalid customer data: {}", res);
      throw new BadInputException(res.stream().findFirst().get().getMessage());
    }
  }
}
