package com.peterso.esledgerbankingserver.model.dto;

import com.peterso.esledgerbankingserver.validation.PosOrZeroMonetaryString;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.With;

/**
 * Amount DTO that follows the given Schema.
 * Contains the:
 * monetary amount (String) *checked to be positive or zero, and at most 2 decimals,
 * currency of the amount (String),
 * type of amount (DEBIT or CREDIT)
 */
@Data
@AllArgsConstructor
@Builder
public class Amount {

  @PosOrZeroMonetaryString
  private String amount;
  @NotBlank(message = "currency must not be blank")
  private String currency;
  @With
  private DebitCreditEnum debitOrCredit;
}
