package io.berndruecker.ticketbooking.adapter;

import java.io.IOException;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import io.berndruecker.ticketbooking.ProcessConstants;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;

@Component
public class GenerateTicketAdapter {

  Logger logger = LoggerFactory.getLogger(GenerateTicketAdapter.class);

  // This should be of course injected and depends on the environment.
  // Hard coded for simplicity here
  public static String ENDPOINT = "http://localhost:3000/ticket";

  @Autowired
  private RestTemplate restTemplate;

  @ZeebeWorker(type = "generate-ticket")
  public void callGenerateTicketRestService(final JobClient client, final ActivatedJob job) throws IOException {
    logger.info("Generate ticket via REST [" + job + "]");

    if ("ticket".equalsIgnoreCase((String)job.getVariablesAsMap().get(ProcessConstants.VAR_SIMULATE_BOOKING_FAILURE))) {

      // Simulate a network problem to the HTTP server
      throw new IOException("[Simulated] Could not connect to HTTP server");
      
    } else {
      
      // Call REST API, simply returns a ticketId
      CreateTicketResponse ticket = restTemplate.getForObject(ENDPOINT, CreateTicketResponse.class);  
      logger.info("Succeeded with " + ticket);
      
      client.newCompleteCommand(job.getKey()) //
        .variables(Collections.singletonMap(ProcessConstants.VAR_TICKET_ID, ticket.ticketId)) //
        .send()
        .exceptionally(throwable -> { throw new RuntimeException("Could not complete job " + job, throwable); });
    }
  }

  public static class CreateTicketResponse {
    public String ticketId;
  }
}
