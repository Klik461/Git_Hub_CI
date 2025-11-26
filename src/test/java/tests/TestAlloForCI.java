package tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*; // Залишаємо лише один імпорт, щоб включити TakesScreenshot, File, OutputType
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestResult; // *** ДОДАНО ДЛЯ ОБРОБКИ РЕЗУЛЬТАТІВ ТЕСТУ ***
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File; // Для роботи з файлами
import java.io.IOException; // Для обробки винятків при роботі з файлами
import java.nio.file.Files; // Для копіювання файлів
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    // *** ОНОВЛЕННЯ: Метод приймає ITestResult, щоб перевірити статус тесту ***
    @AfterMethod
    public void quitDriver(ITestResult result) {
        if (driver != null) {
            // Перевіряємо, чи тест не пройшов (статус FAILURE)
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

        // *** ВАЖЛИВО: Наявність NoSuchElementException (що призводить до TimeoutException) відбувається тут ***
        WebElement alloLogo = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='tad-logo']"))
        );

        // Якщо елемент присутній, ми прокручуємо до нього
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", alloLogo);

        // Тепер перевіряємо, чи він відображається.
        Assert.assertTrue(alloLogo.isDisplayed());
    }

    // *** НОВИЙ ДОПОМІЖНИЙ МЕТОД ДЛЯ СТВОРЕННЯ СКРІНШОТА ***
    public void takeScreenshot(String testName) {
        if (driver instanceof TakesScreenshot) {
            // 1. Створюємо унікальне ім'я файлу
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
            String fileName = testName + "_" + timestamp + ".png";

            // 2. Визначаємо цільову директорію у корені проекту
            File screenshotDir = new File("target/screenshots");
            if (!screenshotDir.exists()) {
                screenshotDir.mkdirs(); // Створюємо директорію, якщо її немає
            }
            File destFile = new File(screenshotDir, fileName);

            try {
                // 3. Робимо знімок екрана
                File sourceFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

                // 4. Копіюємо файл
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
    // *****************************************************************
}