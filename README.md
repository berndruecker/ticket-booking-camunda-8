# ticket-booking-camunda-cloud

A ticket booking example using Camunda Cloud, RabbitMQ and two sample apps (Java Spring Boot and NodeJS)


```
docker run -d -p 15672:15672 -p 5672:5672 rabbitmq:latest
```

Run node

```
ts-node fake-services-nodejs/src/app.ts
```

Run Java

```
mvn package exec:java -f booking-service-java\
```

```
 curl -X PUT http://localhost:8080/ticket
```