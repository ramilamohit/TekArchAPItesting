package com.tekArch.base;

import org.testng.annotations.BeforeSuite;
import com.tekArch.utilities.EnvironmentDetails;
import com.tekArch.utilities.TestDataUtils;

public class BaseTest {
    @BeforeSuite
    public void beforeSuite() {
        EnvironmentDetails.loadProperties();
        TestDataUtils.loadProperties();
    }

}
