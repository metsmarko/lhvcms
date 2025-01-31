package com.metsmarko.lhvcms.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ExceptionControllerAdviceTest {

  private final ExceptionControllerAdvice advice = new ExceptionControllerAdvice();

  @Test
  void handleUnknownError() {
    ResponseEntity<ProblemDetail> resp = advice.handleUnknownError(new Error("err"), null);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
    assertNull(resp.getBody().getDetail());
  }
}
