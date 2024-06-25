package com.peterso.esledgerbankingserver.repository;

import com.peterso.esledgerbankingserver.model.event.TransactionEvent;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * An in-memory event store represented by an ArrayList.
 */
@Repository
@Getter
@NoArgsConstructor
public class InMemoryEventStore implements EventStore {

  private final List<TransactionEvent> eventStore = new ArrayList<>();

  @Override
  public void saveEvent(TransactionEvent event) {
    eventStore.add(event);
  }
}
