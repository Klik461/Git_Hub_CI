package tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfAllElementsLocatedBy;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class TestAlloForCI {
    WebDriver driver;
    WebDriverWait wait;

    private static final String WINDOW_SIZE = "--window-size=1920,1080";
    private final static String BASE_URL = "https://www.timeanddate.com/worldclock/";

    @BeforeClass
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

        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    @BeforeMethod
    public void openUrl() {
        driver.get(BASE_URL);
    }

    @AfterMethod
    public void takeScreenShotIfTestFailed(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            takeScreenshot(result.getMethod().getMethodName());
        }
    }

    @AfterClass
    public void quitDriver() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void checkAlloLogo() {
        assertTrue(wait.until(presenceOfElementLocated(
                By.xpath("//div[@class='tad-logo']"))).isDisplayed());
    }

    @Test
    public void checkSizeCitiesOnPage() {
        List<WebElement> listCompanyName = wait.until(presenceOfAllElementsLocatedBy(By.xpath("//div[@class='tb-scroll']//span[@class='wds']/../a")));

        assertEquals(listCompanyName.size(), 143);

        listCompanyName.stream()
                .map(WebElement::getText)
                .map(name -> "___" + name)
                .forEach(System.out::println);

        assertTrue(listCompanyName.stream()
                .map(WebElement::getText)
                .anyMatch(name -> name.contains("Tokyo")));
    }

    private void takeScreenshot(String testName) {
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