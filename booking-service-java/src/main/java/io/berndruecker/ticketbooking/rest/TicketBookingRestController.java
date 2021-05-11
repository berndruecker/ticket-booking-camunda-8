package io.berndruecker.ticketbooking.rest;

import java.util.HashMap;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import io.berndruecker.ticketbooking.ProcessConstants;
import io.berndruecker.ticketbooking.adapter.RetrievePaymentAdapter;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.ZeebeFuture;
import io.camunda.zeebe.client.api.command.ClientStatusException;
import io.camunda.zeebe.client.api.response.ProcessInstanceResult;
import io.camunda.zeebe.spring.client.EnableZeebeClient;

@SpringBootConfiguration
@RestController
@EnableZeebeClient
public class TicketBookingRestController {

  private Logger logger = LoggerFactory.getLogger(RetrievePaymentAdapter.class);

  @Autowired
  private ZeebeClient client;

  @PutMapping("/ticket")
  public ResponseEntity<BookTicketResponse> bookTicket(ServerWebExchange exchange) {
    String simulateBookingFailure = exchange.getRequest().getQueryParams().getFirst("simulateBookingFailure");
    
    // This would be best generated even in the client to allow idempotency!
    BookTicketResponse response = new BookTicketResponse();
    response.bookingReferenceId = UUID.randomUUID().toString();
    
    HashMap<String, Object> variables = new HashMap<String, Object>();
    variables.put(ProcessConstants.VAR_BOOKING_REFERENCE_ID, response.bookingReferenceId);
    if (simulateBookingFailure!=null) {
      variables.put(ProcessConstants.VAR_SIMULATE_BOOKING_FAILURE, simulateBookingFailure);
    }

    // Start new instance of the ticket-booking workflow
    ZeebeFuture<ProcessInstanceResult> future = client.newCreateInstanceCommand() //
        .bpmnProcessId("ticket-booking") //
        .latestVersion() //
        .variables(variables) //
        .withResult() // wait for the workflow to finish
        .send(); // with this we get a future

    try {
      // Block until it is really done
      ProcessInstanceResult workflowInstanceResult = future.join();

      // Unwrap data from workflow after it finished
      response.reservationId = (String) workflowInstanceResult.getVariablesAsMap().get(ProcessConstants.VAR_RESERVATION_ID);
      response.paymentConfirmationId = (String) workflowInstanceResult.getVariablesAsMap().get(ProcessConstants.VAR_PAYMENT_CONFIRMATION_ID);
      response.ticketId = (String) workflowInstanceResult.getVariablesAsMap().get(ProcessConstants.VAR_TICKET_ID);
      
      return ResponseEntity.status(HttpStatus.OK).body(response);
    } catch (ClientStatusException ex) {

      // of course we can run into a timeout if the workflow does not finish
      // within that timeframe!
      logger.error("Timeout on waiting for workflow"); //, ex);

      return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
  }
  
  // TODO: Add API to query status (if you got an 202 earlier on)

  public static class BookTicketResponse {
    public String reservationId;
    public String paymentConfirmationId;
    public String ticketId;   
    public String bookingReferenceId;

    public boolean isSuccess() {
      return (ticketId != null);
    }
  }
}
