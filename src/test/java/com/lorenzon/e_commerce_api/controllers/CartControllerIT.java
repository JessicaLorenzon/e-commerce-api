package com.lorenzon.e_commerce_api.controllers;

import com.lorenzon.e_commerce_api.factories.TokenUtil;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class CartControllerIT {

    private String userEmail;
    private String userPassword;
    private String userToken;
    private String invalidToken;
    private Long existingProductId;
    private Long nonExistingProductId;
    private Map<String, Object> itemInstance;

    @BeforeEach
    public void setUp() {
        baseURI = "http://localhost:8080";

        userEmail = "ana@ana.com";
        userPassword = "123456";

        userToken = TokenUtil.obtainAccessToken(userEmail, userPassword);
        invalidToken = userToken + "xpto";

        itemInstance = new HashMap<>();
        itemInstance.put("productId", 1);
        itemInstance.put("quantity", 1);
    }

    @Test
    public void insertItemShouldReturnCreatedWhenRequestIsValid() {
        given()
                .header("Authorization", "Bearer " + userToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(itemInstance)
                .when()
                .post("/carts")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("items.size()", greaterThan(0));
    }

    @Test
    public void insertItemShouldReturnBadRequestWhenQuantityIsInvalid() {
        itemInstance.put("quantity", -1);

        given()
                .header("Authorization", "Bearer " + userToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(itemInstance)
                .when()
                .post("/carts")
                .then()
                .statusCode(400)
                .body("detail", equalTo("Field(s) with invalid value(s)"));
    }

    @Test
    public void insertItemShouldReturnBadRequestWhenProductIdIsNull() {
        itemInstance.put("productId", null);

        given()
                .header("Authorization", "Bearer " + userToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(itemInstance)
                .when()
                .post("/carts")
                .then()
                .statusCode(400)
                .body("detail", equalTo("Field(s) with invalid value(s)"));
    }

    @Test
    public void insertItemShouldReturnNotFoundWhenProductDoesNotExist() {
        itemInstance.put("productId", 1000);

        given()
                .header("Authorization", "Bearer " + userToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(itemInstance)
                .when()
                .post("/carts")
                .then()
                .statusCode(404);
    }

    @Test
    public void insertItemShouldReturnBadRequestWhenQuantityExceedsStock() {
        itemInstance.put("quantity", 1000);

        given()
                .header("Authorization", "Bearer " + userToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(itemInstance)
                .when()
                .post("/carts")
                .then()
                .statusCode(400);
    }

    @Test
    public void insertItemShouldIncreaseQuantityWhenProductAlreadyExistsInCart() {
        int quantityBefore =
                given()
                        .header("Authorization", "Bearer " + userToken)
                        .accept(ContentType.JSON)
                        .when()
                        .get("/carts")
                        .then()
                        .statusCode(200)
                        .extract()
                        .path("items[1].quantity");

        given()
                .header("Authorization", "Bearer " + userToken)
                .contentType(ContentType.JSON)
                .body(itemInstance)
                .when()
                .post("/carts")
                .then()
                .statusCode(201)
                .body("items[1].quantity", equalTo(quantityBefore + 1));
    }

    @Test
    public void insertItemShouldReturnUnauthorizedWhenTokenIsInvalid() {
        given()
                .header("Authorization", "Bearer " + invalidToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(itemInstance)
                .when()
                .post("/carts")
                .then()
                .statusCode(401);
    }

    @Test
    public void updateItemShouldReturnOkWhenRequestIsValid() {
        given()
                .header("Authorization", "Bearer " + userToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(itemInstance)
                .when()
                .put("/carts")
                .then()
                .statusCode(200);
    }

    @Test
    public void updateItemShouldReturnNotFoundWhenProductIsNotInCart() {
        itemInstance.put("productId", 3);

        given()
                .header("Authorization", "Bearer " + userToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(itemInstance)
                .when()
                .put("/carts")
                .then()
                .statusCode(404)
                .body("detail", equalTo("Item with ID 3 not found"));
    }

    @Test
    public void deleteItemShouldReturnNoContentWhenProductExistsInCart() {
        existingProductId = 1L;

        given()
                .header("Authorization", "Bearer " + userToken)
                .when()
                .delete("/carts/{productId}", existingProductId)
                .then()
                .statusCode(204);
    }

    @Test
    public void deleteItemShouldReturnNotFoundWhenProductIsNotInCart() {
        existingProductId = 2L;

        given()
                .header("Authorization", "Bearer " + userToken)
                .when()
                .delete("/carts/{productId}", existingProductId)
                .then()
                .statusCode(404)
                .body("detail", equalTo("Item with ID " + existingProductId + " not found"));
    }
}
