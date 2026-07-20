package com.lorenzon.e_commerce_api.controllers;

import com.lorenzon.e_commerce_api.services.PaymentService;
import com.lorenzon.e_commerce_api.services.StripeService;
import com.stripe.exception.EventDataObjectDeserializationException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private StripeService stripeService;

    @PostMapping("/checkout")
    public ResponseEntity<String> checkout() throws StripeException {
        String url = paymentService.checkout();
        return ResponseEntity.ok(url);
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String header) throws EventDataObjectDeserializationException, SignatureVerificationException {
        Event event = stripeService.constructEvent(payload, header);
        switch (event.getType()) {
            case "checkout.session.completed":
                Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
                if (session != null) {
                    paymentService.handleSuccessEvent(session.getId());
                    System.out.println("PAID");
                }
                break;
            case "payment_intent.payment_failed":
                var paymentIntent = (PaymentIntent) event.getDataObjectDeserializer().getObject().orElse(null);
                if (paymentIntent != null) {
                    String orderId = paymentIntent.getMetadata().get("orderId");
                    paymentService.handleFailEvent(Long.parseLong(orderId));
                    System.out.println("FAILED");
                    System.out.println("Reason: " + paymentIntent.getLastPaymentError().getMessage());
                }
            default:
                break;
        }
        return ResponseEntity.ok().build();
    }
}
