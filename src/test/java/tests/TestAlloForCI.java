package tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

public class TestAlloForCI {
    WebDriver driver;

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

            options.addArguments("--disable-gpu");
            options.addArguments("--disable-features=IsolateOrigins,site-per-process");
            options.addArguments("--remote-allow-origins=*");
        }

        driver = new ChromeDriver(options);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(90));

        if (!isHeadless) {
            driver.manage().window().maximize();
        }
    }

    @AfterMethod
    public void quitDriver(ITestResult result) {
        if (driver != null) {
            if (result.getStatus() == ITestResult.FAILURE) {
                takeScreenshot(result.getMethod().getMethodName());
            }
            driver.quit();
        }
    }

    @Test
    public void checkAlloLogo() {
        String baseUrl = "https://www.timeanddate.com/worldclock/";

        driver.get(baseUrl);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        WebElement alloLogo = wait.until(presenceOfElementLocated(By.xpath("//div[@class='tad-logo']")));

        Assert.assertTrue(alloLogo.isDisplayed());
    }

    public void takeScreenshot(String testName) {
        if (driver instanceof TakesScreenshot) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
            String fileName = testName + "_" + timestamp + ".png";

            File screenshotDir = new File("target/screenshots");
            if (!screenshotDir.exists()) {
                screenshotDir.mkdirs();
            }
            File destFile = new File(screenshotDir, fileName);

            try {
                File sourceFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

                Files.copy(sourceFile.toPath(), destFile.toPath());
                System.out.println("🖼️ Знімок екрана збережено: " + destFile.getAbsolutePath());

            } catch (IOException e) {
                System.err.println("Помилка при збереженні скріншота: " + e.getMessage());
            } catch (WebDriverException e) {
                // Це може статися, якщо драйвер уже закритий або нестабільний
                System.err.println("Помилка WebDriver при спробі зробити скріншот: " + e.getMessage());
            }
        }
    }

}