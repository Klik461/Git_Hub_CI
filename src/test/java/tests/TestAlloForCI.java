package tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor; // Импорт для скролінгу
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

public class TestAlloForCI {
    WebDriver driver;
    SoftAssert softAssert = new SoftAssert();

    // Визначаємо новий, менший розмір
    private static final String WINDOW_SIZE = "--window-size=1280,720";

    @BeforeMethod
    public void setUpDriver() {
        ChromeOptions options = new ChromeOptions();
        WebDriverManager.chromedriver().setup();

        options.addArguments("--disable-notifications");

        // Встановлюємо менший розмір вікна для Headless і Local
        options.addArguments(WINDOW_SIZE);

        String headlessProp = System.getProperty("headless", "false");
        boolean isHeadless = Boolean.parseBoolean(headlessProp);

        if (isHeadless) {
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
        }
        driver = new ChromeDriver(options);

        // Якщо тест запускається не в headless, максимізуємо вікно для зручності налагодження,
        // але в headless керуємо розміром через options
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

        // Чекаємо видимість елемента
        WebElement alloLogo = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@class='v-logo']"))
        );

        // Скролінг, щоб гарантувати, що елемент потрапив у viewport
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", alloLogo);

        softAssert.assertTrue(alloLogo.isDisplayed(), "Allo logo is not displayed after waiting and scrolling.");
        softAssert.assertAll();
    }
}