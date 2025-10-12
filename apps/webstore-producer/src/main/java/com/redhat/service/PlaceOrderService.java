package com.redhat.service;

import org.jboss.logging.Logger;

import com.redhat.infrastructure.KafkaOrderProducer;
import com.redhat.model.Order;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PlaceOrderService {
    private static final Logger LOG = Logger.getLogger(PlaceOrderService.class);
    
    @Inject
    KafkaOrderProducer producer;

    public void place(Order order) {
        LOG.infof("Processing order %s for sending to Kafka.", order.getOrderId());

        producer.sendOrder(order);
    }
}
