package com.peterso.esledgerbankingserver.repository;

import com.peterso.esledgerbankingserver.model.event.TransactionEvent;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * Interface for the EventStore repository.
 */
@Repository
public interface EventStore {

  /**
   * Stores the event in the repository.
   * @param event to save
   */
  void saveEvent(TransactionEvent event);

  /**
   * Returns a list of events representative of the eventStore.
   * @return
   */
  List<TransactionEvent> getEventStore();
}
