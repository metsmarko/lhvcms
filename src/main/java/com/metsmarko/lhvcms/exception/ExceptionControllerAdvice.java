package com.metsmarko.lhvcms.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class ExceptionControllerAdvice {
  private static final Logger log = LoggerFactory.getLogger(ExceptionControllerAdvice.class);

  @ExceptionHandler(NotFoundException.class)
  protected ResponseEntity<ProblemDetail> handleNotFound(Throwable ex, WebRequest request) {
    return ResponseEntity
        .of(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage()))
        .build();
  }

  @ExceptionHandler(BadInputException.class)
  protected ResponseEntity<ProblemDetail> handleBadInput(Throwable ex, WebRequest request) {
    return ResponseEntity
        .of(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage()))
        .build();
  }

  @ExceptionHandler(Exception.class)
  protected ResponseEntity<ProblemDetail> handleUnknownError(Throwable ex, WebRequest request) {
    log.error("internal server error", ex);
    return ResponseEntity.of(ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR)).build();
  }
}
