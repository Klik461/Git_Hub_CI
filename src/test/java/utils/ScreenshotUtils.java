package utils; // Пакет для утиліт

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScreenshotUtils {

    private static final String SCREENSHOT_DIR = "target/screenshots";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    private ScreenshotUtils() {
    }

    public static void takeScreenshot(WebDriver driver, String testName) {
        // Перевіряємо, чи підтримує драйвер скріншоти
        if (driver instanceof TakesScreenshot) {

            String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
            String fileName = String.format("%s_%s.png", testName, timestamp);

            try {
                // 1. Створення директорії (якщо вона ще не існує)
                Files.createDirectories(Paths.get(SCREENSHOT_DIR));
                File destFile = new File(SCREENSHOT_DIR, fileName);

                // 2. Отримання скріншота
                File sourceFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

                // 3. Копіювання файлу
                Files.copy(sourceFile.toPath(), destFile.toPath());
                System.out.println("🖼️ Знімок екрана збережено: " + destFile.getAbsolutePath());

            } catch (IOException e) {
                System.err.println("Помилка при збереженні скріншота: " + e.getMessage());
            } catch (WebDriverException e) {
                // Запобігаємо падінню, якщо драйвер нестабільний
                System.err.println("Помилка WebDriver при спробі зробити скріншот: " + e.getMessage());
            }
        }
    }
}