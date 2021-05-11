package io.berndruecker.ticketbooking.adapter;

import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.camunda.zeebe.client.ZeebeClient;

@Component
public class PaymentAmqpReceiver {
  
  private Logger logger = LoggerFactory.getLogger(RetrievePaymentAdapter.class);

  @Autowired
  private ZeebeClient client;
  
  @Autowired
  private ObjectMapper objectMapper;
  
  @RabbitListener(queues = "paymentResponse")
  @Transactional  
  public void messageReceived(String paymentResponseString) throws JsonMappingException, JsonProcessingException {
    PaymentResponseMessage paymentResponse = objectMapper.readValue(paymentResponseString, PaymentResponseMessage.class);
    logger.info("Received " + paymentResponse);
    
    // Route message to workflow
    client.newPublishMessageCommand() //
      .messageName("msg-payment-received") //
      .correlationKey(paymentResponse.paymentRequestId) //
      .variables(Collections.singletonMap("paymentConfirmationId", paymentResponse.paymentConfirmationId)) //
      .send().join();
  }
  
  public static class PaymentResponseMessage {
    public String paymentRequestId;
    public String paymentConfirmationId;
    public String toString() {
      return "PaymentResponseMessage [paymentRequestId=" + paymentRequestId + ", paymentConfirmationId=" + paymentConfirmationId + "]";
    }
  }

}
