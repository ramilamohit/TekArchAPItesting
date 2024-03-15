package com.tekArch.tests.login;

import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.tekArch.ResponsePOJO.StatusResponsePOJO;
import com.tekArch.base.APIHelper;
import com.tekArch.base.BaseTest;
import com.tekArch.utilities.EnvironmentDetails;
import com.tekArch.utilities.ExtentReportsUtility;
import com.tekArch.utilities.JsonSchemaValidate;
import com.tekArch.utilities.TestDataUtils;
import io.restassured.response.Response;

@Listeners(com.tekArch.listeners.TestEventListenersUtility.class)

public class ValidateLoginAPI_Functionality extends BaseTest
{
	ExtentReportsUtility report;
    APIHelper apiHelper;
    
    @BeforeClass
    public void beforeClass() {
        apiHelper = new APIHelper();
        report = ExtentReportsUtility.getInstance();
    }

    @Test(priority = 0, description = "validate login functionality with valid credentials")
    public void validateLoginWithValidCredentials() {
        Response login = apiHelper.login(EnvironmentDetails.getProperty("username"), EnvironmentDetails.getProperty("password"));
        Assert.assertEquals(login.getStatusCode(), HttpStatus.SC_CREATED,"error occured with login");
        report.logTestInfo("successfull login with statuscode 201");
        JsonSchemaValidate.validateSchemaInClassPath(login, "ExpectedJsonSchema/LoginResponseSchema.json");
        report.logTestInfo("LoginResponse is validated against expected schema successfully");
       
    }
    
    @Test(priority = 1, description = "validate login functionality with invalid credentials")
    public void validateLoginWithInValidCredentials() {
        Response login = apiHelper.login(EnvironmentDetails.getProperty("username"), "password");
        Assert.assertEquals(login.getStatusCode(), HttpStatus.SC_UNAUTHORIZED, "Login is not returning proper status code with invalid credentials.");
        StatusResponsePOJO statusResponse = login.as(StatusResponsePOJO.class);
        Assert.assertEquals(statusResponse.getStatus(), TestDataUtils.getProperty("invalidCredentialsMessage"), "Status message is not returning as expected");
    }


}
