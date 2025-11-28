package tests;

import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import utils.DriverManager;
import utils.ScreenshotUtils;

public class BaseTest {

    protected final static String BASE_URL = "https://intertop.ua/";

    @BeforeClass
    public void setUpDriver() {
        DriverManager.setupDriver();
    }

    @BeforeMethod
    public void openUrl() {
        DriverManager.getDriver().get(BASE_URL);
    }

    @AfterMethod
    public void takeScreenShotIfTestFailed(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            ScreenshotUtils.takeScreenshot(DriverManager.getDriver(), result.getMethod().getMethodName());
        }
    }

    @AfterClass
    public void dropBrowser() {
        DriverManager.quitDriver();
    }

    protected WebDriver getDriver() {
        return DriverManager.getDriver();
    }
}