package com.lorenzon.e_commerce_api.services;

import com.lorenzon.e_commerce_api.entities.order.Order;
import com.lorenzon.e_commerce_api.entities.orderItem.OrderItem;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class StripeService {

    @Value("${stripe.webhook-secret}")
    private String webhookKey;

    public Session createSession(Order order) throws StripeException {

        List<SessionCreateParams.LineItem> lineItems = order.getItems()
                .stream()
                .map(this::mapToLineItem)
                .toList();
        SessionCreateParams params = createSessionCreateParams(order.getId(), lineItems);
        return Session.create(params);
    }

    private SessionCreateParams.LineItem mapToLineItem(OrderItem item) {
        return SessionCreateParams.LineItem.builder()
                .setQuantity((long) (item.getQuantity()))
                .setPriceData(
                        SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("brl")
                                .setUnitAmount((item.getPrice().multiply(BigDecimal.valueOf(100)).longValueExact()))
                                .setProductData(
                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                .setName(item.getProduct().getName())
                                                .build()
                                )
                                .build()
                )
                .build();
    }

    private SessionCreateParams createSessionCreateParams(Long orderId, List<SessionCreateParams.LineItem> lineItems) {
        return SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:8080/success")
                .setCancelUrl("http://localhost:8080/cancel")
                .putMetadata("orderId", String.valueOf(orderId))
                .setPaymentIntentData(
                        SessionCreateParams.PaymentIntentData.builder()
                                .putMetadata("orderId", String.valueOf(orderId))
                                .build()
                )
                .addAllLineItem(lineItems)
                .build();
    }

    public Event constructEvent(String payload, String header) throws SignatureVerificationException {
        return Webhook.constructEvent(payload, header, webhookKey);
    }
}
