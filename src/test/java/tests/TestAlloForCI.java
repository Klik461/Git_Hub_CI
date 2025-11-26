package tests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.time.Duration;

import static java.lang.Thread.sleep;

public class TestAlloForCI {
    WebDriver driver;
    SoftAssert softAssert = new SoftAssert();

    @BeforeMethod
    public void setUpDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--window-size=1920,1080");

        String headlessProp = System.getProperty("headless", "false");
        boolean isHeadless = Boolean.parseBoolean(headlessProp);

        if (isHeadless) {
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
        }
        driver = new ChromeDriver(options);
    }

    @AfterMethod
    public void quitDriver() {
        if (driver != null) driver.quit();
    }

    @Test
    public void checkAlloLogo() {
        String baseUrl = "https://allo.ua/";

        driver.get(baseUrl);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement alloLogo = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@class='v-logo']"))
        );

        softAssert.assertTrue(alloLogo.isDisplayed());
        softAssert.assertAll();
    }

}
