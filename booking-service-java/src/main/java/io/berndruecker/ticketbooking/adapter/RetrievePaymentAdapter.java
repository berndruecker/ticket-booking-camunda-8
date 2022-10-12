package io.berndruecker.ticketbooking.adapter;

import io.berndruecker.ticketbooking.ProcessConstants;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@Component
public class RetrievePaymentAdapter {
  
  private Logger logger = LoggerFactory.getLogger(RetrievePaymentAdapter.class);
  
  public static String RABBIT_QUEUE_NAME = "paymentRequest";
  
  @Autowired
  protected RabbitTemplate rabbitTemplate;
  
  @JobWorker(type = "retrieve-payment")
  public Map<String, Object> retrievePayment(final ActivatedJob job) {
      logger.info("Send message to retrieve payment [" + job + "]");
      
      // create correlation id for this request/response cycle
      String paymentRequestId = UUID.randomUUID().toString();
      
      // Send AMQP Message (using the default exchange created, see https://stackoverflow.com/questions/43408096/springamqp-rabbitmq-how-to-send-directly-to-queue-without-exchange)
      rabbitTemplate.convertAndSend(RABBIT_QUEUE_NAME, paymentRequestId);
            
      return Collections.singletonMap(ProcessConstants.VAR_PAYMENT_REQUEST_ID, paymentRequestId);
  }
}
