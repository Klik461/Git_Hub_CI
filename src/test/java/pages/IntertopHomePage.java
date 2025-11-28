package pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.stream.Collectors;

public class IntertopHomePage extends BasePage {

    public IntertopHomePage() {
        super();
    }

    @FindBy(xpath = "//div[@class='in-navbar__sections']/a[@aria-current='page']")
    private WebElement logo;

    @FindBy(xpath = "//div[@class='in-navbar__sections']//p[@class='truncate']")
    private List<WebElement> listChooseMale;

    public WebElement intertopLogo() {
        return waitForVisibility(logo);
    }

    public List<WebElement> listCatagoryCatalog() {
        return waitForAllVisibility(listChooseMale);
    }

    public List<String> getValueCatagoryCatalog() {
        return listCatagoryCatalog().stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

}