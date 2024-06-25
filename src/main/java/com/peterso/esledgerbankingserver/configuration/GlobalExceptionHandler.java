package com.peterso.esledgerbankingserver.configuration;

import com.peterso.esledgerbankingserver.model.dto.Error;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Exception Handler on the global level to customize validation error handling.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  // Method to handle MethodArgumentNotValidException thrown from jakarta.validations.
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Error> handleValidationErrors(MethodArgumentNotValidException e) {
    // Extracts error messages from the exception.
    List<String> errors = e.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(FieldError::getDefaultMessage).collect(Collectors.toList());

    // Creates the custom error object with the formatted error messages.
    Error errorResponse = new Error(formatMessages(errors),
        Optional.of(HttpStatus.BAD_REQUEST.toString()));

    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  // Utility method to format a list of messages into a single method.
  private String formatMessages(List<String> errors) {
    return StringUtils.join(errors, ',');
  }
}
