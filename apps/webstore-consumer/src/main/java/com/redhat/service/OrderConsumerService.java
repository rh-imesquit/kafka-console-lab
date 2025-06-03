package com.redhat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhat.model.Order;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

@ApplicationScoped
public class OrderConsumerService {

    private static final Logger LOG = Logger.getLogger(OrderConsumerService.class);

    @Inject
    ObjectMapper mapper;

    @Incoming("orders")
    public void consume(String payload) {
        try {
            Order order = mapper.readValue(payload, Order.class);
            LOG.infof("Received order %s from customer %s with items: %s",
                    order.getOrderId(), order.getCustomerId(), order.getItems());
        } catch (Exception e) {
            LOG.error("Failed to deserialize order: " + payload, e);
        }
    }
}