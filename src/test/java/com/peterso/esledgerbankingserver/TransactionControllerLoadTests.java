package com.peterso.esledgerbankingserver;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peterso.esledgerbankingserver.model.dto.Amount;
import com.peterso.esledgerbankingserver.model.dto.DebitCreditEnum;
import com.peterso.esledgerbankingserver.model.dto.Error;
import com.peterso.esledgerbankingserver.model.dto.LoadRequest;
import com.peterso.esledgerbankingserver.model.dto.LoadResponse;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
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
public class TransactionControllerLoadTests {

  private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

  @Autowired
  private MockMvc mockMvc;

  @Disabled
  @Test
  void whenGivenValidRequest_ShouldSucceedAndReturnResponse() throws Exception {

    LoadRequest loadRequest = LoadRequest.builder()
        .userId("1")
        .messageId("1")
        .transactionAmount(Amount.builder()
            .amount("10")
            .currency("USD")
            .debitOrCredit(DebitCreditEnum.CREDIT)
            .build())
        .build();

    LoadResponse validLoadResponse = LoadResponse.builder()
        .userId("1")
        .messageId("1")
        .balance(Amount.builder()
            .amount("10.00")
            .currency("USD")
            .debitOrCredit(DebitCreditEnum.CREDIT)
            .build())
        .build();

    ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
            .put("/load")
            .content(objectMapper.writeValueAsString(loadRequest))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated());

    MvcResult result = resultActions.andReturn();

    String contentAsString = result.getResponse().getContentAsString();

    LoadResponse response = objectMapper.readValue(contentAsString, LoadResponse.class);
    assertThat(response).usingRecursiveComparison().isEqualTo(validLoadResponse);
  }

  @Test
  void whenGivenInvalidMessageId_thenShouldReturnError() throws Exception {
    LoadRequest invalidMessageIdRequest = LoadRequest.builder()
        .userId("1")
        .messageId("")
        .transactionAmount(Amount.builder()
            .amount("10")
            .currency("USD")
            .debitOrCredit(DebitCreditEnum.CREDIT)
            .build())
        .build();

    ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
            .put("/load")
            .content(objectMapper.writeValueAsString(invalidMessageIdRequest))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

    MvcResult result = resultActions.andReturn();

    String contentAsString = result.getResponse().getContentAsString();

    Error response = objectMapper.readValue(contentAsString, Error.class);
    assertEquals("messageId must not be blank", response.getMessage());
  }

  @Test
  void whenGivenInvalidUserId_thenShouldReturnError() throws Exception {
    LoadRequest invalidUserIdRequest = LoadRequest.builder()
        .userId("")
        .messageId("1")
        .transactionAmount(Amount.builder()
            .amount("10")
            .currency("USD")
            .debitOrCredit(DebitCreditEnum.CREDIT)
            .build())
        .build();

    ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
            .put("/load")
            .content(objectMapper.writeValueAsString(invalidUserIdRequest))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

    MvcResult result = resultActions.andReturn();

    String contentAsString = result.getResponse().getContentAsString();

    Error response = objectMapper.readValue(contentAsString, Error.class);
    assertEquals("userId must not be blank", response.getMessage());
  }

  @Test
  void whenGivenNegativeAmount_thenShouldReturnError() throws Exception {
    LoadRequest invalidAmountRequest = LoadRequest.builder()
        .userId("1")
        .messageId("1")
        .transactionAmount(Amount.builder()
            .amount("-10")
            .currency("USD")
            .debitOrCredit(DebitCreditEnum.CREDIT)
            .build())
        .build();

    ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
            .put("/load")
            .content(objectMapper.writeValueAsString(invalidAmountRequest))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

    MvcResult result = resultActions.andReturn();

    String contentAsString = result.getResponse().getContentAsString();

    Error response = objectMapper.readValue(contentAsString, Error.class);
    assertEquals("String must be zero or a positive numerical monetary representation",
        response.getMessage());
  }

  @Test
  void whenGivenNonNumericalAmount_thenShouldReturnError() throws Exception {
    LoadRequest invalidAmountRequest = LoadRequest.builder()
        .userId("1")
        .messageId("1")
        .transactionAmount(Amount.builder()
            .amount("10.3444")
            .currency("USD")
            .debitOrCredit(DebitCreditEnum.CREDIT)
            .build())
        .build();

    ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
            .put("/load")
            .content(objectMapper.writeValueAsString(invalidAmountRequest))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

    MvcResult result = resultActions.andReturn();

    String contentAsString = result.getResponse().getContentAsString();

    Error response = objectMapper.readValue(contentAsString, Error.class);
    assertEquals("String must be zero or a positive numerical monetary representation",
        response.getMessage());
  }

  @Test
  void whenGivenDebitAmount_thenShouldReturnError() throws Exception {
    LoadRequest invalidDebitAmountRequest = LoadRequest.builder()
        .userId("1")
        .messageId("1")
        .transactionAmount(Amount.builder()
            .amount("10")
            .currency("USD")
            .debitOrCredit(DebitCreditEnum.DEBIT)
            .build())
        .build();

    ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
            .put("/load")
            .content(objectMapper.writeValueAsString(invalidDebitAmountRequest))
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