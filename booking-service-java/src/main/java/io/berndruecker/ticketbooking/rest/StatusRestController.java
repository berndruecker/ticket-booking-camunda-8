package io.berndruecker.ticketbooking.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.zeebe.client.api.response.Topology;
import io.zeebe.spring.client.EnableZeebeClient;
import io.zeebe.spring.client.ZeebeClientLifecycle;

@SpringBootConfiguration
@RestController
@EnableZeebeClient
public class StatusRestController {
    
    @Autowired
    private ZeebeClientLifecycle client;

    @GetMapping("/status")
    public String getStatus() {
        Topology topology = client.newTopologyRequest().send().join();
        return topology.toString();
    }
    
}
