package com.peterso.esledgerbankingserver;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peterso.esledgerbankingserver.controller.TransactionController;
import com.peterso.esledgerbankingserver.model.dto.Amount;
import com.peterso.esledgerbankingserver.model.dto.AuthorizationRequest;
import com.peterso.esledgerbankingserver.model.dto.AuthorizationResponse;
import com.peterso.esledgerbankingserver.model.dto.DebitCreditEnum;
import com.peterso.esledgerbankingserver.model.dto.Error;
import com.peterso.esledgerbankingserver.model.dto.LoadRequest;
import com.peterso.esledgerbankingserver.model.dto.ResponseCodeEnum;
import com.peterso.esledgerbankingserver.service.TransactionService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
public class TransactionControllerAuthorizationTests {

  private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

  @Autowired
  private MockMvc mockMvc;

  @Mock
  TransactionService transactionService;

  @InjectMocks
  TransactionController transactionController;

  @Test
  void whenGivenValidRequest_ShouldSucceedAndReturnResponse() throws Exception {

    ResultActions loadAction = mockMvc.perform(MockMvcRequestBuilders
            .put("/load")
            .content(objectMapper.writeValueAsString(LoadRequest.builder()
                .userId("1")
                .messageId("1")
                .transactionAmount(Amount.builder()
                    .amount("20.00")
                    .currency("USD")
                    .debitOrCredit(DebitCreditEnum.CREDIT).build()).build()))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated());

    AuthorizationRequest validAuthorizationRequest = AuthorizationRequest.builder()
        .userId("1")
        .messageId("2")
        .transactionAmount(Amount.builder()
            .amount("10.00")
            .currency("USD")
            .debitOrCredit(DebitCreditEnum.DEBIT)
            .build())
        .build();

    AuthorizationResponse validAuthorizationResponse = AuthorizationResponse.builder()
        .userId("1")
        .messageId("2")
        .responseCode(ResponseCodeEnum.APPROVED)
        .balance(Amount.builder()
            .amount("10.00")
            .currency("USD")
            .debitOrCredit(DebitCreditEnum.DEBIT)
            .build())
        .build();

    ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
            .put("/authorization")
            .content(objectMapper.writeValueAsString(validAuthorizationRequest))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated());

    MvcResult result = resultActions.andReturn();

    String contentAsString = result.getResponse().getContentAsString();

    AuthorizationResponse response = objectMapper.readValue(contentAsString,
        AuthorizationResponse.class);
    assertThat(response).usingRecursiveComparison().isEqualTo(validAuthorizationResponse);
  }

  @Test
  void whenGivenInvalidMessageId_thenShouldReturnError() throws Exception {
    AuthorizationRequest invalidMessageIdRequest = AuthorizationRequest.builder()
        .userId("1")
        .messageId("")
        .transactionAmount(Amount.builder()
            .amount("10")
            .currency("USD")
            .debitOrCredit(DebitCreditEnum.DEBIT)
            .build())
        .build();

    ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
            .put("/authorization")
            .content(objectMapper.writeValueAsString(invalidMessageIdRequest))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

    MvcResult result = resultActions.andReturn();

    String contentAsString = result.getResponse().getContentAsString();

    Error response = objectMapper.readValue(contentAsString, Error.class);
    assertEquals(
        "messageId must not be blank",
        response.getMessage());
  }

  @Test
  void whenGivenInvalidUserId_thenShouldReturnError() throws Exception {
    AuthorizationRequest invalidUserIdRequest = AuthorizationRequest.builder()
        .userId("")
        .messageId("1")
        .transactionAmount(Amount.builder()
            .amount("10")
            .currency("USD")
            .debitOrCredit(DebitCreditEnum.DEBIT)
            .build())
        .build();

    ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
            .put("/authorization")
            .content(objectMapper.writeValueAsString(invalidUserIdRequest))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

    MvcResult result = resultActions.andReturn();

    String contentAsString = result.getResponse().getContentAsString();

    Error response = objectMapper.readValue(contentAsString, Error.class);
    assertEquals(
        "userId must not be blank",
        response.getMessage());
  }

  @Test
  void whenGivenNegativeAmount_thenShouldReturnError() throws Exception {
    AuthorizationRequest invalidAmountRequest = AuthorizationRequest.builder()
        .userId("1")
        .messageId("1")
        .transactionAmount(Amount.builder()
            .amount("-10")
            .currency("USD")
            .debitOrCredit(DebitCreditEnum.DEBIT)
            .build())
        .build();

    ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
            .put("/authorization")
            .content(objectMapper.writeValueAsString(invalidAmountRequest))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

    MvcResult result = resultActions.andReturn();

    String contentAsString = result.getResponse().getContentAsString();

    Error response = objectMapper.readValue(contentAsString, Error.class);
    assertEquals(
        "String must be zero or a positive numerical monetary representation",
        response.getMessage());
  }

  @Test
  void whenGivenNonNumericalAmount_thenShouldReturnError() throws Exception {
    AuthorizationRequest invalidAmountRequest = AuthorizationRequest.builder()
        .userId("1")
        .messageId("1")
        .transactionAmount(Amount.builder()
            .amount("10.101")
            .currency("USD")
            .debitOrCredit(DebitCreditEnum.DEBIT)
            .build())
        .build();

    ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
            .put("/authorization")
            .content(objectMapper.writeValueAsString(invalidAmountRequest))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

    MvcResult result = resultActions.andReturn();

    String contentAsString = result.getResponse().getContentAsString();

    Error response = objectMapper.readValue(contentAsString, Error.class);
    assertEquals(
        "String must be zero or a positive numerical monetary representation",
        response.getMessage());
  }

  @Test
  void whenGivenCreditAmount_thenShouldReturnError() throws Exception {
    AuthorizationRequest invalidCreditRequest = AuthorizationRequest.builder()
        .userId("1")
        .messageId("1")
        .transactionAmount(Amount.builder()
            .amount("10")
            .currency("USD")
            .debitOrCredit(DebitCreditEnum.CREDIT)
            .build())
        .build();

    ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
            .put("/authorization")
            .content(objectMapper.writeValueAsString(invalidCreditRequest))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

    MvcResult result = resultActions.andReturn();

    String contentAsString = result.getResponse().getContentAsString();

    Error response = objectMapper.readValue(contentAsString, Error.class);
    assertEquals(
        "Transaction type does not match amount type. Ensure that a Load request contains CREDIT and an Authorization request contains DEBIT",
        response.getMessage());
  }
}