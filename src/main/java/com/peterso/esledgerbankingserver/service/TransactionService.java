package com.peterso.esledgerbankingserver.service;

import com.peterso.esledgerbankingserver.model.dto.Amount;
import com.peterso.esledgerbankingserver.model.dto.AuthorizationRequest;
import com.peterso.esledgerbankingserver.model.dto.AuthorizationResponse;
import com.peterso.esledgerbankingserver.model.dto.DebitCreditEnum;
import com.peterso.esledgerbankingserver.model.dto.LoadRequest;
import com.peterso.esledgerbankingserver.model.dto.LoadResponse;
import com.peterso.esledgerbankingserver.model.dto.ResponseCodeEnum;
import com.peterso.esledgerbankingserver.model.dto.TransactionTypeEnum;
import com.peterso.esledgerbankingserver.model.event.TransactionEvent;
import com.peterso.esledgerbankingserver.repository.EventStore;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The service for the transaction business logic of the ledger service.
 */
@Service
@AllArgsConstructor
public class TransactionService {

  @Autowired
  private EventStore inMemoryEventStore;

  /**
   * Processes a LoadRequest made by the client by making a representative event, saving the event
   * in the eventStore, and responding with a LoadResponse with the user's updated balance.
   *
   * @param command {@link LoadRequest}
   * @return {@link LoadResponse}
   */
  public LoadResponse handleLoadRequest(LoadRequest command) {
    String userId = command.getUserId();
    String messageId = command.getMessageId();
    Amount commandAmount = command.getTransactionAmount();

    // create event with corresponding data
    TransactionEvent event = TransactionEvent.builder()
        .transactionType(TransactionTypeEnum.LOAD)
        .messageId(command.getMessageId())
        .amount(commandAmount)
        .userId(userId)
        .response(ResponseCodeEnum.APPROVED)
        .build();

    // save event to eventStore
    inMemoryEventStore.saveEvent(event);

    // retrieve updated user balance
    BigDecimal newUserBalance = this.getBalance(userId);

    // return with the corresponding LoadResponse
    return LoadResponse.builder()
        .userId(event.getUserId())
        .messageId(event.getMessageId())
        .balance(Amount.builder()
            .amount(newUserBalance.toPlainString())
            .currency(event.getAmount().getCurrency())
            .debitOrCredit(DebitCreditEnum.CREDIT)
            .build())
        .build();
  }

  /**
   * Processes an AuthorizationRequest made by the client by making a representative event, saving
   * the event in the eventStore, and responding with a LoadResponse with the user's updated
   * balance.
   *
   * @param command {@link AuthorizationRequest}
   * @return {@link AuthorizationResponse}
   */
  public AuthorizationResponse handleAuthorizationRequest(AuthorizationRequest command) {
    String userId = command.getUserId();
    String messageId = command.getMessageId();
    Amount commandAmount = command.getTransactionAmount();

    // Build base event for authorization
    TransactionEvent event = TransactionEvent.builder()
        .messageId(command.getMessageId())
        .userId(userId)
        .transactionType(TransactionTypeEnum.AUTHORIZATION)
        .amount(commandAmount)
        .build();

    // retrieve current user balance
    BigDecimal userBalance = this.getBalance(userId);

    // retrieve authorization amount
    BigDecimal commandAmountBD = new BigDecimal(commandAmount.getAmount());

    // compare authorization amount with current balance to decide if authorization
    // is approved or declined and update the event.
    if (commandAmountBD.compareTo(userBalance) > 0) {
      event = event.withResponse(ResponseCodeEnum.DECLINED);
    } else {
      event = event.withResponse(ResponseCodeEnum.APPROVED);
    }

    // save event into the eventStore
    inMemoryEventStore.saveEvent(event);

    // retrieve updated user balance
    BigDecimal newUserBalance = this.getBalance(userId);

    // return the corresponding AuthorizationResponse
    return AuthorizationResponse.builder()
        .userId(event.getUserId())
        .messageId(event.getMessageId())
        .responseCode(event.getResponse())
        .balance(Amount.builder()
            .amount(newUserBalance.toPlainString())
            .currency(event.getAmount().getCurrency())
            .debitOrCredit(DebitCreditEnum.DEBIT)
            .build())
        .build();
  }

  /**
   * Calculates the balance for the given user by recreating the user's balance through the events
   *
   * @param userId
   * @return the final balance of the user as a BigDecimal
   */
  public BigDecimal getBalance(String userId) {
    List<TransactionEvent> events = inMemoryEventStore.getEventStore();

    BigDecimal userBalance = events.stream()
        .filter(event -> event.getUserId().equals(userId) &&
            ResponseCodeEnum.APPROVED == event.getResponse())
        .map(event -> {
          BigDecimal amount = new BigDecimal(event.getAmount().getAmount());
          return switch (event.getTransactionType()) {
            case LOAD -> amount;
            case AUTHORIZATION -> amount.negate();
          };
        })
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    return userBalance.setScale(2);
  }
}
