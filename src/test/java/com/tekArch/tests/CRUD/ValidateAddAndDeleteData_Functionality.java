package com.tekArch.tests.CRUD;

import java.util.List;

import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import com.github.javafaker.Faker;
import com.tekArch.RequestPOJO.AddDataRequestPOJO;
import com.tekArch.RequestPOJO.DeleteDataRequestPOJO;
import com.tekArch.ResponsePOJO.AddDataResponsePOJO;
import com.tekArch.ResponsePOJO.GetDataResponsePOJO;
import com.tekArch.ResponsePOJO.LoginResponsePOJO;
import com.tekArch.ResponsePOJO.StatusResponsePOJO;
import com.tekArch.base.APIHelper;
import com.tekArch.utilities.EnvironmentDetails;
import com.tekArch.utilities.ExtentReportsUtility;
import com.tekArch.utilities.JsonSchemaValidate;
import com.tekArch.utilities.TestDataUtils;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;

@Listeners(com.tekArch.listeners.TestEventListenersUtility.class)

public class ValidateAddAndDeleteData_Functionality {
    APIHelper apiHelper;
    String userId, accountNo, departmentNo, salary, pincode;
    private Faker faker;
    String dataId = "";
	ExtentReportsUtility report;

    @BeforeClass
    public void beforeClass() {
        faker = new Faker();
        apiHelper = new APIHelper();
        report = ExtentReportsUtility.getInstance();
        Response login = apiHelper.login(EnvironmentDetails.getProperty("username"), EnvironmentDetails.getProperty("password"));
        userId = login.getBody().as(new TypeRef<List<LoginResponsePOJO>>() {}).get(0).getUserid();
    }

    @Test(priority = 0, description = "validate add data functionality")
    public void validateAddDataFunctionality() {
        accountNo = "TA-" + faker.number().numberBetween(10000, 20000);
        departmentNo = "5";
        salary = faker.number().numberBetween(15000, 85000) + "";
        pincode = faker.address().zipCode();
        AddDataRequestPOJO addDataRequest = AddDataRequestPOJO.builder().accountNo(accountNo).departmentNo(departmentNo).salary(salary).pinCode(pincode).build();
        Response response = apiHelper.addData(addDataRequest);
        report.logTestInfo("Account number got created "+ accountNo);
        Assert.assertEquals(response.getStatusCode(), HttpStatus.SC_CREATED, "Add data functionality is not working as expected.");
        report.logTestInfo("Status code" + response.getStatusCode());
        Assert.assertEquals(response.as(AddDataResponsePOJO.class).getStatus(), TestDataUtils.getProperty("successStatusMessage"), "The value of status key is not as expected in response ");
        JsonSchemaValidate.validateSchema(response.asPrettyString(), "StatusResponseSchema.json");

    }


    @Test(priority = 1, description = "validate added data in the get data object", dependsOnMethods = "validateAddDataFunctionality")
    public void validateAddedDataInGetData() {
        Response data = apiHelper.getData();
        List<GetDataResponsePOJO> getDataResponseList = data.getBody().as(new TypeRef<List<GetDataResponsePOJO>>() {
        });
        Assert.assertEquals(data.getStatusCode(), HttpStatus.SC_OK, "Response code is not matching for get data.");
        GetDataResponsePOJO getDataResponse = null;
        try {
            getDataResponse = returnTheMatchingGetDataResponse(accountNo, userId, getDataResponseList);
        } catch (NullPointerException e) {
            Assert.fail("Added data is not available in the get data response");
        }
        dataId = getDataResponse.getId();
        report.logTestInfo("Department number: "+ getDataResponse.getDepartmentNo());
        Assert.assertEquals(getDataResponse.getDepartmentNo(), departmentNo, "Add data functionality is not working as expected, Department number is not matching");
        Assert.assertEquals(getDataResponse.getSalary(), salary, "Add data functionality is not working as expected, Salary is not matching");
        Assert.assertEquals(getDataResponse.getPinCode(), pincode, "Add data functionality is not working as expected, Pincode is not matching");
    }

    @Test(priority = 2, description = "delete data functionality", dependsOnMethods = "validateAddDataFunctionality")
    public void validateDeleteData() {
        DeleteDataRequestPOJO deleteDataRequest = DeleteDataRequestPOJO.builder().userId(userId).id(dataId).build();
        Response data = apiHelper.deleteData(deleteDataRequest);
        Assert.assertEquals(data.getStatusCode(), HttpStatus.SC_OK, "Delete data functionality is not working as expected.");
        Assert.assertEquals(data.as(StatusResponsePOJO.class).getStatus(), TestDataUtils.getProperty("successStatusMessage"), "The value of status key is not as expected in response ");
        String actualResponse = data.jsonPath().prettyPrint();
        JsonSchemaValidate.validateSchema(actualResponse, "StatusResponseSchema.json");
    }

    @Test(priority = 3, description = "validate deleted data in the get data object", dependsOnMethods = "validateDeleteData")
    public void validateDeletedDataInGetData() {
        Response data = apiHelper.getData();
        List<GetDataResponsePOJO> getDataResponseList = data.getBody().as(new TypeRef<List<GetDataResponsePOJO>>() {});
        Assert.assertEquals(data.getStatusCode(), HttpStatus.SC_OK, "Response code is not matching for get data.");
        if (returnTheMatchingGetDataResponse(accountNo, userId, getDataResponseList) != null) {
            Assert.fail("Deleted data is still available in the get data response");
        }
    }

    public GetDataResponsePOJO returnTheMatchingGetDataResponse(String accountNo, String userId, List<GetDataResponsePOJO> getDataResponseList) {
        for (GetDataResponsePOJO dataResponse : getDataResponseList) {
            if (dataResponse.getAccountNo().equals(accountNo) && dataResponse.getUserId().equals(userId))
                return dataResponse;
        }
        return null;
    }

}
