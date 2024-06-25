package com.peterso.esledgerbankingserver.controller;

import com.peterso.esledgerbankingserver.model.dto.Error;
import com.peterso.esledgerbankingserver.model.dto.Ping;
import com.peterso.esledgerbankingserver.service.LedgerService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for the general ledger service. Contains endpoint such as "ping".
 */
@RestController
public class LedgerController {

  @Autowired
  private LedgerService ledgerService;

  /**
   * GET CRUD operation for '/ping' to easily check if the service is running.
   * Similar to 'health' in other services.
   * @return a ResponseEntity with a {@link Ping} object which contains the server time.
   */
  @GetMapping("/ping")
  public ResponseEntity<?> ping() {
    try {
      String formattedServerTime = ledgerService.getServerTime();
      Ping ping = new Ping(formattedServerTime);
      return new ResponseEntity<>(ping, HttpStatus.OK);
    } catch (Exception e) {
      Error error = new Error(e.toString(), Optional.of(HttpStatus.SERVICE_UNAVAILABLE.toString()));
      return new ResponseEntity<>(error, HttpStatus.SERVICE_UNAVAILABLE);
    }
  }
}
