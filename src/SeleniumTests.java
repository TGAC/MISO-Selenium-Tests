import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map.Entry;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class SeleniumTests {

  // Download this! https://github.com/mozilla/geckodriver/releases
  private static final String GECKODRIVER_PATH = "C:\\\\code\\\\geckodriver.exe";

  // MISO Creds
  private static final String USERNAME = "miso_user";
  private static final String PASSWORD = "miso_password";

  private static final String BASE_URL = "http:/127.0.0.1:8080/";

  private static final JsonObject pageTests = new JsonObject();
  private static WebDriver driver;

  private static void initTests() {
    // left menu

    addPageTest("miso/sample/receipt", "Receive Samples — MISO LIMS", "Sample Information");
    addPageTest("miso/importexport", "MISO LIMS", "Import the Sample Sheet and based on the entries, save sample QC info.");
    addPageTest("miso/samples", "Samples — MISO LIMS", "Samples");
    addPageTest("miso/libraries", "Libraries — MISO LIMS", "Libraries");
    addPageTest("miso/pools", "Pools — MISO LIMS", "Illumina Pools");
    addPageTest("miso/containers", "Sequencing Containers — MISO LIMS", "Sequencing Containers");
    addPageTest("miso/runs", "Runs — MISO LIMS", "Runs");
    addPageTest("miso/boxes", "Boxes — MISO LIMS", "Boxes");
    addPageTest("miso/sequencers", "Sequencers — MISO LIMS", "Sequencers");
    addPageTest("miso/kitdescriptors", "Kits — MISO LIMS", "Sequencing Kits");
    addPageTest("miso/indices", "Indices — MISO LIMS", "Indices");
    addPageTest("miso/experiments", "Experiments — MISO LIMS", "Experiments");
    addPageTest("miso/studies", "Studies — MISO LIMS", "Studies");
    addPageTest("miso/printjobs", "Print Jobs — MISO LIMS", "Printers");
    addPageTest("miso/custombarcode", "MISO LIMS", "Custom Barcode Printing");

    addPageTest("miso/admin/configuration/printers", "Configure Printers — MISO LIMS", "Printers");
    addPageTest("miso/admin/users", "Users — MISO LIMS", "Logged-in Users");
    addPageTest("miso/admin/groups", "Groups — MISO LIMS", "Add Group");

    // tabs
    addPageTest("miso/mainMenu", "Home — MISO LIMS", "Logged in as:");
    addPageTest("miso/myAccount", "My Account — MISO LIMS", "My Account");
    addPageTest("miso/projects", "Projects — MISO LIMS", "Projects");
    addPageTest("miso/flexreports", "MISO LIMS", "Projects Report");

  }

  @Test
  public void testPages() throws Exception {
    System.setProperty("webdriver.gecko.driver", GECKODRIVER_PATH);
    initTests();
    driver = new FirefoxDriver();
    login();
    runPageTests();
    driver.quit();
  }

  private static void addPageTest(String expectedUrl, String expectedTitle, String mustBeOnPage) {
    JsonObject conds = new JsonObject();
    conds.addProperty("expectedUrl", BASE_URL + expectedUrl);
    conds.addProperty("expectedTitle", expectedTitle);
    conds.addProperty("mustBeOnPage", mustBeOnPage);
    pageTests.add(BASE_URL + expectedUrl, conds);
  }

  private static void runPageTests() {
    for (Entry<String, JsonElement> entry : pageTests.entrySet()) {
      String url = entry.getKey();
      String title = entry.getValue().getAsJsonObject().get("expectedTitle").getAsString();
      String mustBeOnPage = entry.getValue().getAsJsonObject().get("mustBeOnPage").getAsString();
      driver.get(url);
      assertPageCorrect(url, title, mustBeOnPage);
    }
  }

  private static void assertPageCorrect(String expectedUrl, String expectedTitle, String mustBeOnPage) {
    assertEquals(expectedUrl, driver.getCurrentUrl());
    assertEquals(expectedTitle, driver.getTitle());
    assertTrue(driver.getPageSource().contains(mustBeOnPage));
  }

  private static void login() {
    driver.get(BASE_URL + "login.jsp");
    WebElement usernameField = driver.findElement(By.name("j_username"));
    WebElement passwordField = driver.findElement(By.name("j_password"));

    usernameField.click();
    usernameField.sendKeys(USERNAME);
    System.out.println(usernameField.getText());

    passwordField.click();
    passwordField.sendKeys(PASSWORD);

    driver.findElement(By.name("login")).click();

    (new WebDriverWait(driver, 20)).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver d) {
        if (driver.getPageSource().contains("Bad credentials")) {
          throw new RuntimeException("Bad Credentials!");
        }
        return isElementPresent(By.id("loggedInBanner"));
      }
    });
  }

  private static boolean isElementPresent(By by) {
    try {
      driver.findElement(by);
      return true;
    } catch (NoSuchElementException e) {
      return false;
    }
  }
}