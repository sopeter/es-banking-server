
## Bootstrap instructions

To clean and package the service:
```bash
mvn clean package
```

To run tests:
```bash
mvn test
```

To run the service:
```bash
mvn spring-boot:run
```

## Design considerations
The overall structure of this project follows the event sourcing design pattern to build a simple
bank ledger system. I decided to use Spring Boot 3 to create the REST API since I am most comfortable using
Spring Boot for Java. By doing so, I was able to organize the service into multiple packages and distribute some
of the responsibilities. Specifically, I was able to separate the controller, service, and models so that I can
separate business logic. By doing so, this project can easily be modified and extended for the future.

One major design consideration was not utilizing a middle object between the request and response DTOs. While I had
pondered using a middle model to separate DTOs which is best practice, I believe it would only create more confusion to
the logic for this simple project. Additionally, since the Events are the main source of truth in an Event Source pattern, I
believe that using the Events as the middle model would be sufficient. To put even more emphasis of the Events being the
source of truth, I decided to use the EventStore to calculate a user's balance every time instead of using the User object.

Another design consideration was the use of custom constraint validations to cover most edge cases that could occur by user input.
By doing so, I was able to decrease redundant and direct value checks within the business logic part of the service and instead enforce
a modular validation to occur at the start of the flow.


## Assumptions
One major assumption that was made when designing the service is that additional reads and writes
are not necessary for this state of the service. Since there are only 2 write to the Event Store
(load and authorize) and one read (getting the overall balance), I purposely only implemented the Event
Source design pattern. With additional commands and queries, I would have also implemented the
Command Query Responsibility Segregation (CQRS) pattern to make the service more efficient by segregating
responsibilities.
## Bonus: Deployment considerations
If I were to deploy this service, I would containerize this service using Docker and have the containers managed with
Kubernetes. I would also connect an event store cloud-based event store like Amazon Kinesis or Azure Event Hubs so
that I can easily manage the event stores with no worry of major outage. I would also connect a traditional relational database like PostgreSQL
or NoSQL DB like MongoDB for user data. Finally, I would have some CI/CD pipeline like Jenkins to automate the testing, building, and deployment
of the service with monitoring using Grafana or Datadog.
