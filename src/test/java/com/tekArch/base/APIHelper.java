package com.tekArch.base;

import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.Listeners;

import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tekArch.RequestPOJO.LoginRequestPOJO;
import com.tekArch.RequestPOJO.AddDataRequestPOJO;
import com.tekArch.RequestPOJO.DeleteDataRequestPOJO;
import com.tekArch.RequestPOJO.UpdateDataRequestPOJO;

import com.tekArch.ResponsePOJO.LoginResponsePOJO;
import com.tekArch.utilities.EnvironmentDetails;

import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@Listeners(com.tekArch.listeners.TestEventListenersUtility.class)

@Slf4j
public class APIHelper {
    RequestSpecification reqSpec;
    String token = "";
    
public APIHelper()
{
    RestAssured.baseURI = EnvironmentDetails.getProperty("baseURL");
    reqSpec = RestAssured.given();

}

public Response login(String username, String password)
{
    LoginRequestPOJO loginRequest = LoginRequestPOJO.builder().username(username).password(password).build(); // payload 
    reqSpec.headers(getHeaders(true));
    Response response = null;
    try {
        reqSpec.body(loginRequest); //Serializing loginrequest class to byte stream
         response = reqSpec.post("/login");
        if (response.getStatusCode() == HttpStatus.SC_CREATED) {
            List<LoginResponsePOJO> loginResponse = response.getBody().as(new TypeRef<List<LoginResponsePOJO>>() {});
            this.token = loginResponse.get(0).getToken();
            System.out.println("token====="+token);
        }
    } catch (Exception e) {
        Assert.fail("Login functionality is failing due to :: " + e.getMessage());
    }
    return response;
}

public Response getData() {
    reqSpec = RestAssured.given();
    reqSpec.headers(getHeaders(false));
    Response response = null;
    try {
        response = reqSpec.get("/getdata");
        response.then().log().all();
    } catch (Exception e) {
        Assert.fail("Get data is failing due to :: " + e.getMessage());
    }
    return response;
}

public Response addData(AddDataRequestPOJO addDataRequest) {
    reqSpec = RestAssured.given();
    Response response = null;
    try {
        log.info("Adding below data :: " + new ObjectMapper().writeValueAsString(addDataRequest));
        reqSpec.headers(getHeaders(false));
        reqSpec.body(new ObjectMapper().writeValueAsString(addDataRequest)); //Serializing addData Request POJO classes to byte stream
        response = reqSpec.post("/addData");
        response.then().log().all();
    } catch (Exception e) {
        Assert.fail("Add data functionality is failing due to :: " + e.getMessage());
    }
    return response;
}

public Response putData(UpdateDataRequestPOJO updateDataRequest) {
    reqSpec = RestAssured.given();
    reqSpec.headers(getHeaders(false));
    Response response = null;
    try {
        reqSpec.body(new ObjectMapper().writeValueAsString(updateDataRequest)); //Serializing addData Request POJO classes to byte stream
        response = reqSpec.put("/updateData");
        response.then().log().all();
    } catch (Exception e) {
        Assert.fail("Update data functionality is failing due to :: " + e.getMessage());
    }
    return response;
}

public Response deleteData(DeleteDataRequestPOJO deleteDataRequest) {
    reqSpec = RestAssured.given();
    reqSpec.headers(getHeaders(false));
    Response response = null;
    try {
        reqSpec.body(new ObjectMapper().writeValueAsString(deleteDataRequest)); //Serializing addData Request POJO classes to byte stream
        response = reqSpec.delete("/deleteData");
        response.then().log().all();
    } catch (Exception e) {
        Assert.fail("Delete data functionality is failing due to :: " + e.getMessage());
    }
    return response;
}

public HashMap<String, String> getHeaders(boolean forLogin) {
    HashMap<String, String> headers = new HashMap<>();
    headers.put("Content-Type", "application/json");
    if (!forLogin) {
        headers.put("token", token);
    }
    return headers;
}

}
