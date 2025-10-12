package com.redhat.infrastructure;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhat.model.Order;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class KafkaOrderProducer {
    private static final Logger LOG = Logger.getLogger(KafkaOrderProducer.class);
    
    @Channel("orders")
    Emitter<String> emitter;

    @Inject
    ObjectMapper objectMapper;
    
    public void sendOrder(Order order) {
        try {
            emitter.send(objectMapper.writeValueAsString(order));

            LOG.infof("Order %s successfully sent to Kafka topic.", order.getOrderId());
        } catch (JsonProcessingException exception) {
            throw new RuntimeException("An error occurred while serializing the order.", exception);
        }
    }
}
