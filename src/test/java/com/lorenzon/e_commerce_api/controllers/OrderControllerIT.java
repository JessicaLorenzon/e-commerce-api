package com.lorenzon.e_commerce_api.controllers;

import com.lorenzon.e_commerce_api.factories.TokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class OrderControllerIT {

    private String userEmail;
    private String userPassword;
    private String adminEmail;
    private String adminPassword;
    private String userToken;
    private String adminToken;
    private String invalidToken;
    private Integer userId1;
    private Integer userId2;
    private Integer existingOrderId;
    private Integer nonExistingOrderId;
    private Integer paidOrderId;
    private Integer nonOwnOrderId;

    @BeforeEach
    public void setUp() {
        baseURI = "http://localhost:8080";

        existingOrderId = 1;
        nonExistingOrderId = 100;
        nonOwnOrderId = 22;
        paidOrderId = 20;

        userEmail = "ana@ana.com";
        userPassword = "123456";
        adminEmail = "admin@admin.com";
        adminPassword = "123456";
        userId1 = 1;
        userId2 = 4;

        userToken = TokenUtil.obtainAccessToken(userEmail, userPassword);
        adminToken = TokenUtil.obtainAccessToken(adminEmail, adminPassword);
        invalidToken = userToken + "xpto";
    }

    @Test
    public void findAllShouldReturnOrdersForAuthenticatedUser() {
        given()
                .header("Authorization", "Bearer " + userToken)
                .when()
                .get("/orders")
                .then()
                .statusCode(200)
                .body("userId", everyItem(equalTo(userId1)));
    }

    @Test
    public void findAllShouldReturnAllOrdersWhenUserIsAdmin() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/orders")
                .then()
                .statusCode(200)
                .body("userId", hasItems(userId1, userId2));
    }

    @Test
    public void findAllShouldReturnUnauthorizedWhenUserIsNotAuthenticated() {
        given()
                .header("Authorization", "Bearer " + invalidToken)
                .when()
                .get("/orders")
                .then()
                .statusCode(401);
    }

    @Test
    public void findByIdShouldReturnOrderWhenOrderExists() {
        given()
                .header("Authorization", "Bearer " + userToken)
                .when()
                .get("/orders/{orderId}", existingOrderId)
                .then()
                .statusCode(200)
                .body("id", equalTo(existingOrderId));
    }

    @Test
    public void findByIdShouldReturnNotFoundWhenOrderDoesNotExist() {
        given()
                .header("Authorization", "Bearer " + userToken)
                .when()
                .get("/orders/{orderId}", nonExistingOrderId)
                .then()
                .statusCode(404);
    }

    @Test
    public void findByIdShouldReturnForbiddenWhenUserDoesNotOwnOrder() {
        given()
                .header("Authorization", "Bearer " + userToken)
                .when()
                .get("/orders/{orderId}", nonOwnOrderId)
                .then()
                .statusCode(403);
    }

    @Test
    public void cancelOrderShouldReturnOrderCancelWhenOrderCanBeCanceled() {
        given()
                .header("Authorization", "Bearer " + userToken)
                .when()
                .patch("/orders/{orderId}", existingOrderId)
                .then()
                .statusCode(200)
                .body("status", equalTo("CANCELED"));
    }

    @Test
    public void cancelOrderShouldReturnBadRequestWhenOrderIsAlreadyCanceled() {
        given()
                .header("Authorization", "Bearer " + userToken)
                .when()
                .patch("/orders/{orderId}", existingOrderId)
                .then()
                .statusCode(400)
                .body("detail", equalTo("Order has already been canceled"));
    }

    @Test
    public void cancelOrderShouldReturnBadRequestWhenOrderIsAlreadyPaid() {
        given()
                .header("Authorization", "Bearer " + userToken)
                .when()
                .patch("/orders/{orderId}", paidOrderId)
                .then()
                .statusCode(400)
                .body("detail", equalTo("It is not possible cancel an order already paid"));
    }
}
