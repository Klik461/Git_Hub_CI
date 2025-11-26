package tests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import static java.lang.Thread.sleep;

public class TestAlloForCI {
    WebDriver driver;
    SoftAssert softAssert = new SoftAssert();
    ChromeOptions options = new ChromeOptions();

    boolean headless = false;

    @BeforeMethod
    public void setUpDriver() {
        if (headless) {
            options.addArguments("--headless=new");
            options.addArguments("--window-size=1920,1080");
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
    public void checkAlloLogo() throws InterruptedException {
        String baseUrl = "https://allo.ua/";

        driver.get(baseUrl);

        sleep(5000);
        WebElement alloLogo = driver.findElement(By.xpath("//a[@class='v-logo']"));

        softAssert.assertTrue(alloLogo.isDisplayed());
    }

}
