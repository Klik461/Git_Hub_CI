package tests;

import org.testng.annotations.Test;
import pages.IntertopHomePage;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class IntertopFlowTest extends BaseTest {

    @Test
    public void checkIntertopLogoDisplay() {
        assertTrue(new IntertopHomePage().intertopLogo().isDisplayed());
    }

    @Test
    public void checkCategoriesCountAndContent() {
        IntertopHomePage homePage = new IntertopHomePage();

        assertEquals(homePage.listCatagoryCatalog().size(), 4);

        assertTrue(homePage.getValueCatagoryCatalog().stream()
                .anyMatch(name -> name.contains("Жінкам")));
    }
}