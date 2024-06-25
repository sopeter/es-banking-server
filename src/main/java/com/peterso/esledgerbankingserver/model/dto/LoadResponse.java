package com.peterso.esledgerbankingserver.model.dto;

import com.peterso.esledgerbankingserver.validation.CheckAmountType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

/**
 * The response DTO for a Load transaction following the given Schema.
 * Contains the:
 * userId (string),
 * messageID (string),
 * balance post-load (Amount) *checked for the amount type to be CREDIT
 */
@Data
@Builder
public class LoadResponse {

  @NotBlank(message = "userId must not be blank")
  private String userId;
  @NotBlank(message = "messageId must not be blank")
  private String messageId;
  @Valid
  @CheckAmountType(DebitCreditEnum.CREDIT)
  private Amount balance;
}
