package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.DriverManager;

import java.util.List;

import static java.time.Duration.ofSeconds;
import static org.openqa.selenium.support.PageFactory.initElements;
import static org.openqa.selenium.support.ui.ExpectedConditions.*;

public abstract class BasePage {

    protected final WebDriver driver = DriverManager.getDriver();
    protected final WebDriverWait wait;

    private final static long EXPLICIT_WAIT_SECONDS = 40;

    public BasePage() {
        this.wait = new WebDriverWait(driver, ofSeconds(EXPLICIT_WAIT_SECONDS));
        initElements(driver, this);
    }

    protected WebElement waitForVisibility(WebElement element) {
        return wait.until(visibilityOf(element));
    }

    protected List<WebElement> waitForAllVisibility(List<WebElement> elements) {
        return wait.until(visibilityOfAllElements(elements));
    }

    protected List<WebElement> waitForPresenceOfAll(String elements) {
        return wait.until(presenceOfAllElementsLocatedBy(By.xpath(elements)));
    }
}