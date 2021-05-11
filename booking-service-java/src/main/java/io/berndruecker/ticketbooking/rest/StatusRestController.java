package io.berndruecker.ticketbooking.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.Topology;
import io.camunda.zeebe.spring.client.EnableZeebeClient;

@SpringBootConfiguration
@RestController
@EnableZeebeClient
public class StatusRestController {
    
    @Autowired
    private ZeebeClient client;

    @GetMapping("/status")
    public String getStatus() {
        Topology topology = client.newTopologyRequest().send().join();
        return topology.toString();
    }
    
}
