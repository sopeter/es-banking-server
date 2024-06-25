package com.peterso.esledgerbankingserver.controller;

import com.peterso.esledgerbankingserver.model.dto.AuthorizationRequest;
import com.peterso.esledgerbankingserver.model.dto.AuthorizationResponse;
import com.peterso.esledgerbankingserver.model.dto.Error;
import com.peterso.esledgerbankingserver.model.dto.LoadRequest;
import com.peterso.esledgerbankingserver.model.dto.LoadResponse;
import com.peterso.esledgerbankingserver.service.TransactionService;
import jakarta.validation.Valid;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for transaction related endpoints in the service.
 * Contains PUT operations: '/authorization' and '/load'
 */
@RestController
public class TransactionController {

  @Autowired
  private TransactionService transactionService;

  /**
   * Tries to authorize a transaction request which is similar to a withdrawal request.
   * Validates the given request to ensure the data given contains expected data.
   * @param request: An {@link AuthorizationRequest} which contains data for a user's authorization
   * @return either an {@link AuthorizationResponse} or {@link Error}.
   *  A AuthorizationResponse contains user data post-authorization with a responseCode for whether
   *  the request was accepted or declined.
   *  An Error contains the error message for why the authorization couldn't succeed.
   */
  @PutMapping("/authorization")
  public ResponseEntity<?> authorization(@RequestBody @Valid AuthorizationRequest request) {
    try {
      AuthorizationResponse response = transactionService.handleAuthorizationRequest(request);
      return new ResponseEntity<>(response, HttpStatus.CREATED);
    } catch (Exception e) {
      Error error = new Error(e.getMessage(),
          Optional.of(HttpStatus.INTERNAL_SERVER_ERROR.toString()));
      return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Tries to load a transaction request which is similar to a deposit request.
   * Validates the given request to ensure the data given contains expected data.
   * @param request: A {@link LoadRequest} which contains data for a user's load
   * @return either a {@link LoadResponse} or {@link Error}.
   * A LoadResponse contains user data post-load.
   * An Error contains the error message for why the authorization couldn't succeed.
   */
  @PutMapping("/load")
  public ResponseEntity<?> load(@RequestBody @Valid LoadRequest request) {
    try {
      LoadResponse response = transactionService.handleLoadRequest(request);
      return new ResponseEntity<>(response, HttpStatus.CREATED);
    } catch (Exception e) {
      Error error = new Error(e.getMessage(),
          Optional.of(HttpStatus.INTERNAL_SERVER_ERROR.toString()));
      return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
