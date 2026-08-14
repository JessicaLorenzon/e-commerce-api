package com.lorenzon.e_commerce_api.controllers;

import com.lorenzon.e_commerce_api.factories.TokenUtil;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class ProductControllerIT {

    private String userEmail;
    private String userPassword;
    private String adminEmail;
    private String adminPassword;
    private String userToken;
    private String adminToken;
    private String productName;
    private Integer existingProductId;
    private Integer nonExistingProductId;
    private Map<String, Object> productInstance;


    @BeforeEach
    public void setUp() {
        baseURI = "http://localhost:8080";

        userEmail = "ana@ana.com";
        userPassword = "123456";
        adminEmail = "admin@admin.com";
        adminPassword = "123456";

        userToken = TokenUtil.obtainAccessToken(userEmail, userPassword);
        adminToken = TokenUtil.obtainAccessToken(adminEmail, adminPassword);

        productName = "New Product";

        existingProductId = 1;
        nonExistingProductId = 100;

        productInstance = new HashMap<>();
        productInstance.put("name", "New Product");
        productInstance.put("description", "Lorem ipsum dolor sit amet, consectetur adipiscing elit");
        productInstance.put("price", 100.00);
        productInstance.put("stockQuantity", 10);
    }

    @Test
    public void findAllShouldReturnPaginatedProductsWhenNameNoArgumentsGiven() {
        given()
                .header("Authorization", "Bearer " + userToken)
                .when()
                .get("/products")
                .then()
                .statusCode(200);
    }

    @Test
    public void findAllShouldReturnPaginatedProductsWhenProductNameParamIsNotEmpty() {
        given()
                .header("Authorization", "Bearer " + userToken)
                .when()
                .get("/products?name={productName}", productName)
                .then()
                .statusCode(200)
                .body("content.description[0]", equalTo("Lorem ipsum dolor sit amet, consectetur adipiscing elit"));
    }

    @Test
    public void findByIdShouldReturnProductWhenIdExists() {
        given()
                .header("Authorization", "Bearer " + userToken)
                .when()
                .get("/products/{productId}", existingProductId)
                .then()
                .statusCode(200)
                .body("id", equalTo(existingProductId));
    }

    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() {
        given()
                .header("Authorization", "Bearer " + userToken)
                .when()
                .get("/products/{productId}", nonExistingProductId)
                .then()
                .statusCode(404)
                .body("detail", equalTo("Product with ID " + nonExistingProductId + " not found"));
    }

    @Test
    public void insertShouldReturnCreatedWhenValidProduct() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(productInstance)
                .when()
                .post("/products")
                .then()
                .statusCode(201);
    }

    @Test
    public void insertShouldReturnBadRequestWhenInvalidData() {
        productInstance.put("name", "");

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(productInstance)
                .when()
                .post("/products")
                .then()
                .statusCode(400)
                .body("detail", equalTo("Field(s) with invalid value(s)"));
    }

    @Test
    public void insertShouldReturnForbiddenWhenUserIsNotAdmin() {
        given()
                .header("Authorization", "Bearer " + userToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(productInstance)
                .when()
                .post("/products")
                .then()
                .statusCode(403)
                .body("detail", equalTo("User does not have permission to access this resource"));
    }

    @Test
    public void updateShouldReturnProductWhenValidData() {
        productInstance.put("name", "Update Product");

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(productInstance)
                .when()
                .put("/products/{productId}", existingProductId)
                .then()
                .statusCode(200);
    }

    @Test
    public void updateShouldReturnBadRequestWhenInvalidData() {
        productInstance.put("price", -100.00);

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(productInstance)
                .when()
                .put("/products/{productId}", existingProductId)
                .then()
                .statusCode(400)
                .body("detail", equalTo("Field(s) with invalid value(s)"));
    }

    @Test
    public void updateShouldReturnNotFoundWhenIdDoesNotExist() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(productInstance)
                .when()
                .put("/products/{productId}", nonExistingProductId)
                .then()
                .statusCode(404)
                .body("detail", equalTo("Product with ID " + nonExistingProductId + " not found"));
    }

    @Test
    public void updateShouldReturnForbiddenWhenUserIsNotAdmin() {
        given()
                .header("Authorization", "Bearer " + userToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(productInstance)
                .when()
                .put("/products/{productId}", existingProductId)
                .then()
                .statusCode(403)
                .body("detail", equalTo("User does not have permission to access this resource"));
    }

    @Test
    public void disableShouldReturnProductWithZeroStockWhenProductExists() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(productInstance)
                .when()
                .patch("/products/{productId}", existingProductId)
                .then()
                .statusCode(200)
                .body("stockQuantity", equalTo(0));
    }

    @Test
    public void disableShouldReturnNotFoundWhenIdDoesNotExist() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(productInstance)
                .when()
                .patch("/products/{productId}", nonExistingProductId)
                .then()
                .statusCode(404)
                .body("detail", equalTo("Product with ID " + nonExistingProductId + " not found"));
    }

    @Test
    public void disableShouldReturnForbiddenWhenUserIsNotAdmin() {
        given()
                .header("Authorization", "Bearer " + userToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(productInstance)
                .when()
                .patch("/products/{productId}", existingProductId)
                .then()
                .statusCode(403)
                .body("detail", equalTo("User does not have permission to access this resource"));
    }
}
