package com.lorenzon.e_commerce_api.factories;

import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class TokenUtil {

    public static String obtainAccessToken(String email, String password) {

        JsonPath json = given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "email", email,
                        "password", password
                ))
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath();
        return json.getString("token");
    }
}
