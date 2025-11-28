package utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import static java.lang.System.getProperty;
import static java.time.Duration.ofSeconds;

public class DriverManager {

    private static final ThreadLocal<WebDriver> DRIVER_THREAD_LOCAL = new ThreadLocal<>();

    private static final long PAGE_LOAD_TIMEOUT_SECONDS = 90;
    private static final String WINDOW_SIZE = "--window-size=1920,1080";

    private DriverManager() {
    }

    public static void setupDriver() {
        if (getDriver() == null) {
            ChromeOptions options = new ChromeOptions();
            WebDriverManager.chromedriver().setup();

            options.addArguments("--disable-notifications", WINDOW_SIZE);
            boolean isHeadless = Boolean.parseBoolean(getProperty("headless", "false"));

            if (isHeadless) {
                options.addArguments("--headless=new",
                        "--no-sandbox",
                        "--disable-dev-shm-usage",
                        "--disable-gpu",
                        "--disable-features=IsolateOrigins,site-per-process",
                        "--remote-allow-origins=*");
            }

            WebDriver driver = new ChromeDriver(options);
            driver.manage().timeouts().pageLoadTimeout(ofSeconds(PAGE_LOAD_TIMEOUT_SECONDS));

            if (!isHeadless) {
                driver.manage().window().maximize();
            }

            DRIVER_THREAD_LOCAL.set(driver);
        }
    }

    public static WebDriver getDriver() {
        return DRIVER_THREAD_LOCAL.get();
    }

    public static void quitDriver() {
        if (getDriver() != null) {
            getDriver().quit();
            DRIVER_THREAD_LOCAL.remove();
        }
    }
}