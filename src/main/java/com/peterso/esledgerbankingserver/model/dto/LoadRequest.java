package com.peterso.esledgerbankingserver.model.dto;

import com.peterso.esledgerbankingserver.validation.CheckAmountType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * The request DTO for a Load transaction following the given Schema.
 * Contains the:
 * userId (String),
 * messageId (String)
 * transactionAmount (Amount) *checked for the amount type to be CREDIT
 */
@Getter
@AllArgsConstructor
@Builder
public class LoadRequest {

  @NotBlank(message = "userId must not be blank")
  private String userId;
  @NotBlank(message = "messageId must not be blank")
  private String messageId;
  @Valid
  @CheckAmountType(DebitCreditEnum.CREDIT)
  private Amount transactionAmount;
}
