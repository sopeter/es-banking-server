package com.peterso.esledgerbankingserver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.peterso.esledgerbankingserver.model.dto.Amount;
import com.peterso.esledgerbankingserver.model.dto.AuthorizationRequest;
import com.peterso.esledgerbankingserver.model.dto.AuthorizationResponse;
import com.peterso.esledgerbankingserver.model.dto.DebitCreditEnum;
import com.peterso.esledgerbankingserver.model.dto.LoadRequest;
import com.peterso.esledgerbankingserver.model.dto.LoadResponse;
import com.peterso.esledgerbankingserver.model.dto.ResponseCodeEnum;
import com.peterso.esledgerbankingserver.repository.EventStore;
import com.peterso.esledgerbankingserver.repository.InMemoryEventStore;
import com.peterso.esledgerbankingserver.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TransactionServiceTests {

  private static final Amount AMOUNT_100_23 = Amount.builder()
      .amount("100.23")
      .currency("USD")
      .build();
  private EventStore inMemoryEventStore;
  private TransactionService transactionService;

  @BeforeEach
  void setUp() {
    inMemoryEventStore = spy(new InMemoryEventStore());
    transactionService = new TransactionService(inMemoryEventStore);
  }

  @Nested
  class LoadTests {

    @Test
    void whenFirstTimeValidLoad_thenShouldSucceed() {
      Amount amountRequest = AMOUNT_100_23.withDebitOrCredit(DebitCreditEnum.valueOf("CREDIT"));
      LoadRequest loadRequest = LoadRequest.builder()
          .messageId("1")
          .userId("1")
          .transactionAmount(amountRequest)
          .build();
      LoadResponse expectedResponse = LoadResponse.builder()
          .messageId("1")
          .userId("1")
          .balance(amountRequest)
          .build();

      LoadResponse actualResponse = transactionService.handleLoadRequest(loadRequest);

      assertNotNull(actualResponse);
      assertThat(actualResponse).usingRecursiveComparison().isEqualTo(expectedResponse);
      verify(inMemoryEventStore, times(1)).saveEvent(any());
    }

    @Test
    void whenSecondTimeValidLoad_thenShouldSucceedAndReturnTotal() {
      Amount amountRequest = AMOUNT_100_23.withDebitOrCredit(DebitCreditEnum.valueOf("CREDIT"));
      LoadRequest firstRequest = LoadRequest.builder()
          .messageId("1")
          .userId("1")
          .transactionAmount(amountRequest)
          .build();
      LoadRequest secondRequest = LoadRequest.builder()
          .messageId("2")
          .userId("1")
          .transactionAmount(amountRequest)
          .build();
      LoadResponse expectedResponse = LoadResponse.builder()
          .messageId("2")
          .userId("1")
          .balance(Amount.builder()
              .amount("200.46")
              .currency(amountRequest.getCurrency())
              .debitOrCredit(amountRequest.getDebitOrCredit())
              .build())
          .build();

      LoadResponse firstResponse = transactionService.handleLoadRequest(firstRequest);
      LoadResponse secondResponse = transactionService.handleLoadRequest(secondRequest);

      assertNotNull(firstResponse);
      assertNotNull(secondResponse);
      assertThat(secondResponse).usingRecursiveComparison().isEqualTo(expectedResponse);
      verify(inMemoryEventStore, times(2)).saveEvent(any());
    }

    @Test
    void whenLoadWithoutUserId_thenShouldFail() {
      assertNotNull(inMemoryEventStore);
    }

    @Test
    void whenLoadWithNegAmount_thenShouldFail() {
      assertNotNull(inMemoryEventStore);
    }

    @Test
    void whenLoadWithInvalidAmount_thenShouldFail() {
      assertNotNull(inMemoryEventStore);
    }

    @Test
    void whenLoadWithDebitInAmount_thenShouldFail() {
      // TODO
      assertNotNull(inMemoryEventStore);
    }
  }

  @Nested
  class AuthorizationTests {

    @Test
    void whenAuthorizationWithFirstTimeUser_thenShouldDecline() {
      Amount amountRequest = AMOUNT_100_23.withDebitOrCredit(DebitCreditEnum.valueOf("DEBIT"));
      AuthorizationRequest request = AuthorizationRequest.builder()
          .messageId("1")
          .userId("1")
          .transactionAmount(amountRequest)
          .build();
      AuthorizationResponse expectedResponse = AuthorizationResponse.builder()
          .messageId("1")
          .userId("1")
          .responseCode(ResponseCodeEnum.DECLINED)
          .balance(Amount.builder()
              .amount("0.00")
              .currency("USD")
              .debitOrCredit(DebitCreditEnum.DEBIT)
              .build())
          .build();

      AuthorizationResponse response = transactionService.handleAuthorizationRequest(request);
      assertNotNull(response);
      assertThat(response).usingRecursiveComparison().isEqualTo(expectedResponse);
      verify(inMemoryEventStore, times(1)).saveEvent(any());
    }
  }

  @Nested
  class LoadAndAuthorizationTests {

    private final Amount AMOUNT_100 = Amount.builder()
        .amount("100")
        .currency("USD")
        .build();

    private final Amount AMOUNT_3_23 = Amount.builder()
        .amount("3.23")
        .currency("USD")
        .build();

    private final Amount AMOUNT_10 = Amount.builder()
        .amount("10")
        .currency("USD")
        .build();

    private final Amount AMOUNT_50_01 = Amount.builder()
        .amount("50.01")
        .currency("USD")
        .build();

    @Test
    void whenUserLoadsAndAuthorizesForLess_thenShouldApprove() {
      LoadRequest loadRequest = LoadRequest.builder()
          .userId("1")
          .messageId("1")
          .transactionAmount(AMOUNT_100.withDebitOrCredit(DebitCreditEnum.CREDIT))
          .build();
      AuthorizationRequest authorizationRequest = AuthorizationRequest.builder()
          .userId("1")
          .messageId("2")
          .transactionAmount(AMOUNT_10.withDebitOrCredit(DebitCreditEnum.DEBIT)).build();
      AuthorizationResponse expectedResponse = AuthorizationResponse.builder()
          .userId("1")
          .messageId("2")
          .responseCode(ResponseCodeEnum.APPROVED)
          .balance(Amount.builder()
              .amount("90.00")
              .currency("USD")
              .debitOrCredit(DebitCreditEnum.DEBIT)
              .build())
          .build();

      transactionService.handleLoadRequest(loadRequest);
      AuthorizationResponse response = transactionService.handleAuthorizationRequest(
          authorizationRequest);

      assertNotNull(response);
      assertThat(response).usingRecursiveComparison().isEqualTo(expectedResponse);
      verify(inMemoryEventStore, times(2)).saveEvent(any());
    }

    @Test
    void whenUserLoadsAndAuthorizesForSame_thenShouldApprove() {
      LoadRequest loadRequest = LoadRequest.builder()
          .userId("1")
          .messageId("1")
          .transactionAmount(AMOUNT_100.withDebitOrCredit(DebitCreditEnum.CREDIT))
          .build();
      AuthorizationRequest authorizationRequest = AuthorizationRequest.builder()
          .userId("1")
          .messageId("2")
          .transactionAmount(AMOUNT_100.withDebitOrCredit(DebitCreditEnum.DEBIT)).build();
      AuthorizationResponse expectedResponse = AuthorizationResponse.builder()
          .userId("1")
          .messageId("2")
          .responseCode(ResponseCodeEnum.APPROVED)
          .balance(Amount.builder()
              .amount("0.00")
              .currency("USD")
              .debitOrCredit(DebitCreditEnum.DEBIT)
              .build())
          .build();

      transactionService.handleLoadRequest(loadRequest);
      AuthorizationResponse response = transactionService.handleAuthorizationRequest(
          authorizationRequest);

      assertNotNull(response);
      assertThat(response).usingRecursiveComparison().isEqualTo(expectedResponse);
      verify(inMemoryEventStore, times(2)).saveEvent(any());
    }

    @Test
    void whenUserLoadsAnAuthorizesForMore_thenShouldDecline() {
      LoadRequest loadRequest = LoadRequest.builder()
          .userId("1")
          .messageId("1")
          .transactionAmount(AMOUNT_10.withDebitOrCredit(DebitCreditEnum.CREDIT))
          .build();
      AuthorizationRequest authorizationRequest = AuthorizationRequest.builder()
          .userId("1")
          .messageId("2")
          .transactionAmount(AMOUNT_100.withDebitOrCredit(DebitCreditEnum.DEBIT)).build();
      AuthorizationResponse expectedResponse = AuthorizationResponse.builder()
          .userId("1")
          .messageId("2")
          .responseCode(ResponseCodeEnum.DECLINED)
          .balance(Amount.builder()
              .amount("10.00")
              .currency("USD")
              .debitOrCredit(DebitCreditEnum.DEBIT)
              .build())
          .build();

      transactionService.handleLoadRequest(loadRequest);
      AuthorizationResponse response = transactionService.handleAuthorizationRequest(
          authorizationRequest);

      assertNotNull(response);
      assertThat(response).usingRecursiveComparison().isEqualTo(expectedResponse);
      verify(inMemoryEventStore, times(2)).saveEvent(any());
    }

    @Test
    void givenSampleTestCase_thenShouldSucceed() {
      LoadRequest message1 = LoadRequest.builder()
          .userId("1")
          .messageId("1")
          .transactionAmount(AMOUNT_100.withDebitOrCredit(DebitCreditEnum.CREDIT))
          .build();
      LoadRequest message2 = LoadRequest.builder()
          .userId("1")
          .messageId("2")
          .transactionAmount(AMOUNT_3_23.withDebitOrCredit(DebitCreditEnum.CREDIT))
          .build();
      AuthorizationRequest message3 = AuthorizationRequest.builder()
          .userId("1")
          .messageId("3")
          .transactionAmount(AMOUNT_100.withDebitOrCredit(DebitCreditEnum.DEBIT))
          .build();
      AuthorizationRequest message4 = AuthorizationRequest.builder()
          .userId("1")
          .messageId("4")
          .transactionAmount(AMOUNT_10.withDebitOrCredit(DebitCreditEnum.DEBIT))
          .build();
      AuthorizationRequest message5 = AuthorizationRequest.builder()
          .userId("2")
          .messageId("5")
          .transactionAmount(AMOUNT_50_01.withDebitOrCredit(DebitCreditEnum.DEBIT))
          .build();
      LoadRequest message6 = LoadRequest.builder()
          .userId("2")
          .messageId("6")
          .transactionAmount(AMOUNT_50_01.withDebitOrCredit(DebitCreditEnum.CREDIT))
          .build();
      AuthorizationRequest message7 = AuthorizationRequest.builder()
          .userId("2")
          .messageId("7")
          .transactionAmount(AMOUNT_50_01.withDebitOrCredit(DebitCreditEnum.DEBIT))
          .build();

      LoadResponse response1 = transactionService.handleLoadRequest(message1);
      assertThat(response1.getBalance().getAmount()).isEqualTo("100.00");

      LoadResponse response2 = transactionService.handleLoadRequest(message2);
      assertThat(response2.getBalance().getAmount()).isEqualTo("103.23");

      AuthorizationResponse response3 = transactionService.handleAuthorizationRequest(message3);
      assertThat(response3.getBalance().getAmount()).isEqualTo("3.23");
      assertSame(response3.getResponseCode(), ResponseCodeEnum.APPROVED);

      AuthorizationResponse response4 = transactionService.handleAuthorizationRequest(message4);
      assertThat(response4.getBalance().getAmount()).isEqualTo("3.23");
      assertSame(response4.getResponseCode(), ResponseCodeEnum.DECLINED);

      AuthorizationResponse response5 = transactionService.handleAuthorizationRequest(message5);
      assertThat(response5.getBalance().getAmount()).isEqualTo("0.00");
      assertSame(response5.getResponseCode(), ResponseCodeEnum.DECLINED);

      LoadResponse response6 = transactionService.handleLoadRequest(message6);
      assertThat(response6.getBalance().getAmount()).isEqualTo("50.01");

      AuthorizationResponse response7 = transactionService.handleAuthorizationRequest(message7);
      assertThat(response7.getBalance().getAmount()).isEqualTo("0.00");
      assertSame(response7.getResponseCode(), ResponseCodeEnum.APPROVED);

      verify(inMemoryEventStore, times(7)).saveEvent(any());
    }
  }
}