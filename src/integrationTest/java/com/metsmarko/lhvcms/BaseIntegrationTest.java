package com.metsmarko.lhvcms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.springframework.boot.test.context.SpringBootTest.UseMainMethod.ALWAYS;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT, useMainMethod = ALWAYS)
public abstract class BaseIntegrationTest {
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());

  @LocalServerPort
  private int port;

  @BeforeEach
  protected void setUp() {
    RestAssured.port = port;
  }

  public static RequestSpecification givenHelper() {
    return given().contentType(ContentType.JSON);
  }

  public static RequestSpecification givenHelper(Object body) {
    try {
      return given()
          .contentType(ContentType.JSON)
          .body(OBJECT_MAPPER.writeValueAsString(body));
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
