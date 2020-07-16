# Ticket Booking Example

![Ticket Booking Process](booking-service-java/src/main/resources/ticket-booking.png)

A ticket booking example using 
* Camunda Cloud, 
* RabbitMQ,
* Java Spring Boot App
* NodeJS App

![Architecture Overview](architecture.png)

# How To Run

<a href="http://www.youtube.com/watch?feature=player_embedded&v=m3MYuRKLZa8" target="_blank"><img src="http://img.youtube.com/vi/m3MYuRKLZa8/0.jpg" alt="Walkthrough" width="240" height="180" border="10" /></a>


## Run RabbitMQ locally

```
docker run -p 15672:15672 -p 5672:5672 rabbitmq:3-management
```

http://localhost:15672/#/queues/


## Create Camunda Cloud Cluster

* Login to https://camunda.io/
* Create a new Zeebe cluster
* When the new cluster appears in the console, create a new set of client credentials.
* Copy the client credentials into
  * Java App  `booking-service-java/src/main/resources/application.proeprties`
  * Node App `fake-services-nodejs/.env`


## Run NodeJs Fake Services

The Zeebe related code can be developed from scratch following this get started tutorial: https://docs.cloud.camunda.io/docs/node-client

```
cd fake-services-nodejs
ts-node src/app.ts
```

## Run Java Ticket Booking Service

The Zeebe related code can be developed from scratch following this get started tutorial: https://docs.cloud.camunda.io/docs/spring-java-client

```
mvn package exec:java -f booking-service-java\
```

## Test

```
 curl -X PUT http://localhost:8080/ticket
```

Simulate failures by:

```
curl -X PUT http://localhost:8080/ticket?simulateBookingFailure=seats
curl -X PUT http://localhost:8080/ticket?simulateBookingFailure=ticket
```