package tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
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
import org.openqa.selenium.JavascriptExecutor; // <<< ДОДАНО ІМПОРТ
import org.openqa.selenium.TimeoutException; // <<< ДОДАНО ІМПОРТ

import java.time.Duration;

public class TestAlloForCI {
    WebDriver driver;
    SoftAssert softAssert = new SoftAssert();

    @BeforeMethod
    public void setUpDriver() {
        ChromeOptions options = new ChromeOptions();
        WebDriverManager.chromedriver().setup();

        options.addArguments("--disable-notifications");

        // Встановлення розміру вікна, якщо не в headless
        options.addArguments("--window-size=1920,1080");

        String headlessProp = System.getProperty("headless", "false");
        boolean isHeadless = Boolean.parseBoolean(headlessProp);

        if (isHeadless) {
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");

            // --- ВИПРАВЛЕННЯ: У headless режимі розмір вікна встановлюється через ChromeOptions, а не через Dimension ---
            // options.addArguments("--window-size=1920,1080");  <-- Залишаємо його тут або видаляємо з обох місць і залишаємо лише в опціях

            // Ваш рядок driver.manage().window().setSize(new Dimension(1920, 1080)); був неправильним для BeforeMethod
            // Ми керуємо розміром через опції, які вже задані вище.
        }
        driver = new ChromeDriver(options);

        // Якщо тест запускається не в headless (isHeadless=false), ми можемо використати driver.manage()
        if (!isHeadless) {
            driver.manage().window().maximize();
        }
    }

    @AfterMethod
    public void quitDriver() {
        if (driver != null) driver.quit();
    }

    @Test
    public void checkAlloLogo() {
        String baseUrl = "https://allo.ua/";

        driver.get(baseUrl);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(25));

        WebElement alloLogo = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@class='mh-loc']"))
        );

        // *** ДОДАНО ВИПРАВЛЕННЯ: Скролінг до елемента ***
        // Це гарантує, що елемент потрапляє в зону перегляду (viewport), якщо він прихований
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", alloLogo);
        // **********************************************

        softAssert.assertTrue(alloLogo.isDisplayed(), "Allo logo is not displayed after waiting and scrolling.");
        softAssert.assertAll();
    }
}