package tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;

public class TestAlloForCI {
    WebDriver driver;

    // Визначаємо менший розмір
    private static final String WINDOW_SIZE = "--window-size=1280,720";

    @BeforeMethod
    public void setUpDriver() {
        ChromeOptions options = new ChromeOptions();
        WebDriverManager.chromedriver().setup();

        options.addArguments("--disable-notifications");
        options.addArguments(WINDOW_SIZE);

        String headlessProp = System.getProperty("headless", "false");
        boolean isHeadless = Boolean.parseBoolean(headlessProp);

        if (isHeadless) {
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");

            // *** АГРЕСИВНІ АРГУМЕНТИ ДЛЯ STABILITY НА CI ***
            options.addArguments("--disable-gpu");
            options.addArguments("--disable-features=IsolateOrigins,site-per-process");
            options.addArguments("--remote-allow-origins=*");
            // ***********************************************
        }

        driver = new ChromeDriver(options);

        // Встановлюємо таймаут завантаження сторінки (Page Load)
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));

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

        // *** ЗМІНЕНО: Очікуємо ПРИСУТНОСТІ, а не ВИДИМОСТІ ***
        // Це обходить можливі помилки рендерингу, де елемент є, але не "видимий" для Chrome у Headless
        WebElement alloLogo = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@class='v-logo']"))
        );

        // Якщо елемент присутній, ми прокручуємо до нього, щоб гарантувати, що він у viewport
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", alloLogo);

        // Тепер перевіряємо, чи він відображається.
        Assert.assertTrue(alloLogo.isDisplayed());
    }
}