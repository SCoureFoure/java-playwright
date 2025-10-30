package com.testing.framework.api.functional;

import com.testing.framework.api.client.BaseApiClient;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Sample API test using RestAssured
 */
@Epic("API Testing")
@Feature("User Management")
public class SampleApiTest extends BaseApiClient {
    
    @BeforeClass
    public void setup() {
        logger.info("Setting up API tests");
    }
    
    @Test
    @Story("Get Users")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that we can retrieve a list of users from the API")
    public void testGetUsers() {
        Response response = given()
            .spec(requestSpec)
        .when()
            .get("/users")
        .then()
            .statusCode(200)
            .contentType("application/json")
            .body("size()", greaterThan(0))
            .extract().response();
        
        logger.info("Retrieved {} users", response.jsonPath().getList("$").size());
    }
    
    @Test
    @Story("Get User by ID")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that we can retrieve a specific user by ID")
    public void testGetUserById() {
        // First get all users to find a valid ID
        Response usersResponse = given()
            .spec(requestSpec)
        .when()
            .get("/users")
        .then()
            .statusCode(200)
            .extract().response();
        
        int userId = usersResponse.jsonPath().getInt("[0].id");
        
        Response response = given()
            .spec(requestSpec)
            .pathParam("id", userId)
        .when()
            .get("/users/{id}")
        .then()
            .statusCode(200)
            .body("id", equalTo(userId))
            .extract().response();
        
        logger.info("Retrieved user: {}", response.jsonPath().getString("name"));
    }
    
    @Test
    @Story("Create User")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that we can create a new user")
    public void testCreateUser() {
        String requestBody = """
            {
                "name": "John Doe",
                "email": "john.doe@example.com",
                "age": 30
            }
            """;
        
        Response response = given()
            .spec(requestSpec)
            .body(requestBody)
        .when()
            .post("/users")
        .then()
            .statusCode(anyOf(is(200), is(201)))
            .body("name", equalTo("John Doe"))
            .body("email", equalTo("john.doe@example.com"))
            .extract().response();
        
        logger.info("Created user with ID: {}", response.jsonPath().getString("id"));
    }
    
    @Test
    @Story("Update User")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that we can update an existing user")
    public void testUpdateUser() {
        // First get all users to find a valid ID
        Response usersResponse = given()
            .spec(requestSpec)
        .when()
            .get("/users")
        .then()
            .statusCode(200)
            .extract().response();
        
        int userId = usersResponse.jsonPath().getInt("[0].id");
        
        String requestBody = """
            {
                "name": "Jane Doe Updated",
                "email": "jane.updated@example.com"
            }
            """;
        
        given()
            .spec(requestSpec)
            .pathParam("id", userId)
            .body(requestBody)
        .when()
            .put("/users/{id}")
        .then()
            .statusCode(200)
            .body("name", equalTo("Jane Doe Updated"));
        
        logger.info("Updated user with ID: {}", userId);
    }
    
    @Test
    @Story("Delete User")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that we can delete a user")
    public void testDeleteUser() {
        // Create a user to delete
        String newUser = """
            {
                "name": "User To Delete",
                "email": "delete.me@example.com",
                "role": "Temporary"
            }
            """;
        
        Response createResponse = given()
            .spec(requestSpec)
            .body(newUser)
        .when()
            .post("/users")
        .then()
            .statusCode(anyOf(is(200), is(201)))
            .extract().response();
        
        int userId = createResponse.jsonPath().getInt("id");
        
        given()
            .spec(requestSpec)
            .pathParam("id", userId)
        .when()
            .delete("/users/{id}")
        .then()
            .statusCode(anyOf(is(200), is(204)));
        
        logger.info("Deleted user with ID: {}", userId);
    }
    
    @Test
    @Story("Error Handling")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that API returns 404 for non-existent user")
    public void testGetNonExistentUser() {
        int nonExistentId = 99999;
        
        given()
            .spec(requestSpec)
            .pathParam("id", nonExistentId)
        .when()
            .get("/users/{id}")
        .then()
            .statusCode(404);
        
        logger.info("Verified 404 response for non-existent user");
    }
}
