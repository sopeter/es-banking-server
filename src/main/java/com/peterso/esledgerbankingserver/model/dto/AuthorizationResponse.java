package com.peterso.esledgerbankingserver.model.dto;

import com.peterso.esledgerbankingserver.validation.CheckAmountType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

/**
 * The response DTO for an Authorization transaction following the given Schema.
 * Contains the:
 * userId (string),
 * messageID (string),
 * balance post-authorization (Amount) *checked for the amount type to be DEBIT
 */
@Data
@Builder
public class AuthorizationResponse {

  @NotBlank(message = "userId must not be blank")
  private String userId;
  @NotBlank(message = "messageId must not be blank")
  private String messageId;
  private ResponseCodeEnum responseCode;
  @Valid
  @CheckAmountType(DebitCreditEnum.DEBIT)
  private Amount balance;
}
