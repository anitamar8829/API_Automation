package APITest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

public class APITest {

    String baseURI = "https://reqres.in/api";

    @Test
    public void testGetUsers() {
        // Send GET request to "/users" endpoint with page parameter
        Response response = RestAssured.given()
                .baseUri(baseURI)
                .queryParam("page", 2)
                .when()
                .get("/users");

        // Assert status code
        Assert.assertEquals(response.statusCode(), 200, "Correct status code returned");

        // Log the response body for debugging
        System.out.println(response.getBody().asString());

        // Validate a specific field in the JSON response
        int totalUsers = response.jsonPath().getInt("total");
        Assert.assertTrue(totalUsers > 0, "Total users should be greater than 0");

        // Verify that the first user's email contains "reqres"
        String email = response.jsonPath().getString("data[0].email");
        Assert.assertTrue(email.contains("reqres"), "Email should contain 'reqres'");
    }

    @Test
    public void testCreateUser() {
        // Payload for the POST request
        String jsonBody = "{ \"name\": \"John\", \"job\": \"leader\" }";

        // Send POST request to "/users" endpoint
        Response response = RestAssured.given()
                .baseUri(baseURI)
                .contentType("application/json")
                .body(jsonBody)
                .when()
                .post("/users");

        // Assert status code
        Assert.assertEquals(response.statusCode(), 201, "Correct status code returned");

        // Log the response body for debugging
        System.out.println(response.getBody().asString());

        // Validate a field in the JSON response
        String name = response.jsonPath().getString("name");
        Assert.assertEquals(name, "John", "Name should match the request payload");

        // Validate that the job field is returned
        String job = response.jsonPath().getString("job");
        Assert.assertEquals(job, "leader", "Job should match the request payload");
    }
    @Test
    public void testGetNonExistentUser() {
        // Sending GET request for a non-existent user (user ID 9999)
        Response response = RestAssured.given()
                .baseUri(baseURI)
                .when()
                .get("/users/9999");

        // Assert that the response status code is 404 (Not Found)
        Assert.assertEquals(response.statusCode(), 404, "Expected 404 status code for non-existent user");

        // Print the response body (should be empty or error message)
        System.out.println("Response Body for non-existent user: " + response.getBody().asString());
    }

    // Edge Case 2: Create User with Empty Fields
    @Test
    public void testCreateUserWithEmptyFields() {
        // Empty payload to simulate missing fields
        String emptyBody = "{ \"name\": \"\", \"job\": \"\" }";

        // Sending POST request with empty body
        Response response = RestAssured.given()
                .baseUri(baseURI)
                .contentType("application/json")
                .body(emptyBody)
                .when()
                .post("/users");

        // Assert that the response status code is 201 (Bad Request)
        Assert.assertEquals(response.statusCode(), 201, "Expected 201 status code for missing fields");

        // Log the response body (may return validation errors or an empty response)
        System.out.println("Response for empty fields: " + response.getBody().asString());
    }

    // Edge Case 3: Invalid Data Types (ID as a string instead of a number)
    @Test
    public void testInvalidDataTypeForUserID() {
        // Sending GET request with an invalid ID (string instead of an integer)
        Response response = RestAssured.given()
                .baseUri(baseURI)
                .when()
                .get("/users/abc");

        // Assert that the response status code is 400 (Bad Request) or 404 (Not Found)
        Assert.assertEquals(response.statusCode(), 404, "Expected 404 status code for invalid data type");

        // Log the response body (should contain an error)
        System.out.println("Response for invalid data type: " + response.getBody().asString());
    }

}
