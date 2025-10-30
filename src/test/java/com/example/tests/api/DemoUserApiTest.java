package com.example.tests.api;

import com.testing.framework.core.config.ConfigManager;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class DemoUserApiTest {
    
    @BeforeClass
    public void setup() {
        RestAssured.baseURI = ConfigManager.getInstance().getProperty("api.base.url");
        System.out.println("Testing Demo API at: " + RestAssured.baseURI);
    }
    
    @Test(priority = 1, description = "Test API health endpoint")
    public void testHealthCheck() {
        given()
            .when()
            .get("/health")
            .then()
            .statusCode(200)
            .body("status", equalTo("UP"));
        
        System.out.println("✅ Health check passed - API is running!");
    }
    
    @Test(priority = 2, description = "Get all users from demo API")
    public void testGetAllUsers() {
        Response response = given()
            .when()
            .get("/users")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("size()", greaterThan(0))
            .extract()
            .response();
        
        System.out.println("✅ Found " + response.jsonPath().getList("$").size() + " users");
        System.out.println("Users: " + response.body().asString());
    }
    
    @Test(priority = 3, description = "Get specific user by ID")
    public void testGetUserById() {
        given()
            .pathParam("id", 1)
            .when()
            .get("/users/{id}")
            .then()
            .statusCode(200)
            .body("id", equalTo(1))
            .body("name", notNullValue())
            .body("email", notNullValue());
        
        System.out.println("✅ Successfully retrieved user by ID");
    }
    
    @Test(priority = 4, description = "Create a new user")
    public void testCreateUser() {
        String newUser = """
            {
                "name": "Test User",
                "email": "test@example.com",
                "role": "Tester"
            }
            """;
        
        Response response = given()
            .contentType(ContentType.JSON)
            .body(newUser)
            .when()
            .post("/users")
            .then()
            .statusCode(201)
            .body("name", equalTo("Test User"))
            .body("email", equalTo("test@example.com"))
            .body("role", equalTo("Tester"))
            .body("id", notNullValue())
            .extract()
            .response();
        
        int createdUserId = response.jsonPath().getInt("id");
        System.out.println("✅ Created new user with ID: " + createdUserId);
    }
    
    @Test(priority = 5, description = "Update an existing user")
    public void testUpdateUser() {
        String updatedUser = """
            {
                "name": "Updated User",
                "email": "updated@example.com",
                "role": "Senior Tester"
            }
            """;
        
        given()
            .pathParam("id", 1)
            .contentType(ContentType.JSON)
            .body(updatedUser)
            .when()
            .put("/users/{id}")
            .then()
            .statusCode(200)
            .body("name", equalTo("Updated User"))
            .body("email", equalTo("updated@example.com"))
            .body("role", equalTo("Senior Tester"));
        
        System.out.println("✅ Successfully updated user");
    }
    
    @Test(priority = 6, description = "Delete a user")
    public void testDeleteUser() {
        // First create a user to delete
        String userToDelete = """
            {
                "name": "Delete Me",
                "email": "delete@example.com",
                "role": "Temporary"
            }
            """;
        
        Response createResponse = given()
            .contentType(ContentType.JSON)
            .body(userToDelete)
            .when()
            .post("/users")
            .then()
            .statusCode(201)
            .extract()
            .response();
        
        int userId = createResponse.jsonPath().getInt("id");
        
        // Now delete it
        given()
            .pathParam("id", userId)
            .when()
            .delete("/users/{id}")
            .then()
            .statusCode(204);
        
        System.out.println("✅ Successfully deleted user with ID: " + userId);
        
        // Verify it's gone
        given()
            .pathParam("id", userId)
            .when()
            .get("/users/{id}")
            .then()
            .statusCode(404);
        
        System.out.println("✅ Verified user was deleted");
    }
}
