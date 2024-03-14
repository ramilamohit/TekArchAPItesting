package com.tekArch.utilities;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentReportsUtility {
	public static ExtentReports report ;
	public static ExtentSparkReporter spark;
	public static ExtentTest testLogger;
	private static ExtentReportsUtility extentObject;
	
	private ExtentReportsUtility() {
		
	}
	
	public static ExtentReportsUtility getInstance() {
		if(extentObject==null) {
			extentObject=new ExtentReportsUtility();
		}

		return extentObject;
	}
	
	public void startExtentReport() {
		report= new ExtentReports();
		spark = new ExtentSparkReporter(Constants.EXTENT_REPORT_FILE);
		report.setSystemInfo("Host Name", "TekArchAPITesting");
		report.setSystemInfo("Environment", "QA");
		report.setSystemInfo("User Name", "Ramila");
		
		spark.config().setDocumentTitle("TekArchAPI Test Execution Report");
		spark.config().setReportName("TekArchAPI Regression tests");
		spark.config().setTheme(Theme.DARK);
		report.attachReporter(spark);	
	}
	
	public void startSingleTestReport(String methodName) {
																						
		testLogger=report.createTest(methodName);//Return type Extent test logger- single time creation//on test start//creating test report for methods for 
	}
	public void endReport() {
		report.flush();
	}
	
	public void logTestInfo(String text) {
		//System.out.println("test logger object="+testLogger);
		testLogger.log(Status.INFO,text);
		//testLogger.info(text);
	}
	
	public void logTestpassed(String text) {
		testLogger.log(Status.PASS,MarkupHelper.createLabel(text, ExtentColor.GREEN));
		//testLogger.pass(MarkupHelper.createLabel(text, ExtentColor.GREEN));
	}
	
	public void logTestFailed(String text) {
		testLogger.log(Status.FAIL,MarkupHelper.createLabel(text, ExtentColor.RED));
	}
	
	public void logTestFailedWithException(Throwable e) {
		testLogger.log(Status.FAIL,e);
	
		}
	
	public void logTestSkpied(String text)
	{
		//testLogger.log(Status.SKIP, text);
		testLogger.skip(text);
	}
	
	//onTestFailedWithTimeout
	
	public void logTestFailedWithTimeout(String text)
	{
		testLogger.log(Status.FAIL, text);
	}
	
	public void logTestWithscreenshot(String path) {
		//testLogger.addScreenCaptureFromBase64String(path);
		testLogger.fail(MediaEntityBuilder.createScreenCaptureFromPath(path).build());
		
	}
	
	
	


}
