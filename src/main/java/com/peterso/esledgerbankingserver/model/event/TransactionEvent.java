package com.peterso.esledgerbankingserver.model.event;

import com.peterso.esledgerbankingserver.model.dto.Amount;
import com.peterso.esledgerbankingserver.model.dto.ResponseCodeEnum;
import com.peterso.esledgerbankingserver.model.dto.TransactionTypeEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.With;

/**
 * Transaction Event to be used for Event Sourcing.
 * Contains the:
 * userID for the event (String),
 * messageId for the event (String),
 * transactionType of the event (LOAD or AUTHORIZATION),
 * amount of the transaction (Amount),
 * response of the transaction (APPROVED or DECLINED)
 */
@AllArgsConstructor
@Builder
@Getter
public class TransactionEvent extends Event {

  @NotBlank
  private String userId;
  @NotBlank
  private String messageId;
  private TransactionTypeEnum transactionType;
  @Valid
  private Amount amount;
  @With
  private ResponseCodeEnum response;
}