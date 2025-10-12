package com.redhat.resource;

import java.util.List;
import java.util.UUID;

import org.jboss.logging.Logger;

import com.redhat.model.Order;
import com.redhat.service.PlaceOrderService;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/orders")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class OrderResource {
    private static final Logger LOG = Logger.getLogger(OrderResource.class);

    @Inject
    PlaceOrderService service;

    @POST
    public Response create(OrderRequest request) {
        LOG.infof("Receiving new order for custumer: %s with the following items: %s", 
            request.customerId(), request.items());

        Order order = new Order(UUID.randomUUID().toString(), request.customerId(), request.items());
        service.place(order);
        return Response.accepted().build();
    }

    public record OrderRequest(String customerId, List<String> items) {}
}

