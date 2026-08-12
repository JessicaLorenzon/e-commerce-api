package com.lorenzon.e_commerce_api.controllers;

import com.lorenzon.e_commerce_api.tests.TokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class PaymentControllerIT {

    private String userEmail;
    private String userPassword;
    private String userToken;

    @BeforeEach
    public void setUp() {
        baseURI = "http://localhost:8080";

        userEmail = "ana@ana.com";
        userPassword = "123456";

        userToken = TokenUtil.obtainAccessToken(userEmail, userPassword);
    }

    @Test
    public void checkoutShouldUrlWhenValidRequest() {
        String checkoutUrl =
                given()
                        .header("Authorization", "Bearer " + userToken)
                        .when()
                        .post("/payments/checkout")
                        .then()
                        .statusCode(200)
                        .extract()
                        .asString();

        assertNotNull(checkoutUrl);
        assertFalse(checkoutUrl.isBlank());
        assertTrue(checkoutUrl.startsWith("https://checkout.stripe.com/"));
    }
}
