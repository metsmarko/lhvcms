package com.metsmarko.lhvcms.exception;

import java.io.Serial;

public class BadInputException extends Exception {
  @Serial
  private static final long serialVersionUID = 1L;

  public BadInputException(String message) {
    super(message);
  }
}
