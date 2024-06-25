package com.peterso.esledgerbankingserver.model.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * An Error DTO that follows the given schema.
 * Contains a:
 * message for the error (String),
 * code (String) *optional
 */
@AllArgsConstructor
@Data
public class Error {

  @NotBlank
  private String message;
  private Optional<String> code;
}
