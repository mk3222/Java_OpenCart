package testcases;

import base.Base;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import utils.Utilities;

import java.time.Duration;
import java.util.Objects;

public class Login extends Base {

    private WebDriverWait wait;

    @BeforeMethod
    public void setup() {

        // IMPORTANT: prop must be loaded before prop.getProperty(...)
        loadPropertiesFile();

        // Read browser from config.properties (browserName=chrome) :contentReference[oaicite:3]{index=3}
        String browserName = prop.getProperty("browserName");

        driver = initializeBrowserAndOpenApplicationURL(browserName);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // Navigate to Login page
        Objects.requireNonNull(wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//span[normalize-space()='My Account']")))).click();

        Objects.requireNonNull(wait.until(ExpectedConditions.elementToBeClickable(
                By.linkText("Login")))).click();
    }

    @AfterMethod
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test(priority = 1)
    public void verifyLoginWithValidCredentials() {

        Objects.requireNonNull(wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("input-email"))))
                .sendKeys(prop.getProperty("validEmail"));

        // Config file uses key: ValidPassword (capital V) :contentReference[oaicite:4]{index=4}
        driver.findElement(By.id("input-password"))
                .sendKeys(prop.getProperty("ValidPassword"));

        driver.findElement(By.xpath("//input[@type='submit']")).click();

        WebElement editAccountLink = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.linkText("Edit your account information")));

        Assert.assertNotNull(editAccountLink);
        Assert.assertTrue(editAccountLink.isDisplayed(), "Login was not successful.");
    }

    @Test(priority = 2)
    public void verifyLoginWithInvalidCredentials() {

        // invalid email each run
        Objects.requireNonNull(wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("input-email"))))
                .sendKeys(Utilities.generateEmailWithTimeStamp());

        driver.findElement(By.id("input-password")).sendKeys((dataProp.getProperty("InvalidPassword")));
        driver.findElement(By.xpath("//input[@type='submit']")).click();

        assertWarningMessage();
    }

    @Test(priority = 3)
    public void verifyLoginWithValidEmailAndInvalidPasswordCredentials() {

        Objects.requireNonNull(wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("input-email"))))
                .sendKeys(prop.getProperty("validEmail"));

        driver.findElement(By.id("input-password")).sendKeys(dataProp.getProperty("InvalidPassword"));
        driver.findElement(By.xpath("//input[@type='submit']")).click();

        assertWarningMessage();
    }

    @Test(priority = 4)
    public void verifyLoginWithInvalidEmailAndValidPasswordCredentials() {

        Objects.requireNonNull(wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("input-email")))).clear();
        driver.findElement(By.id("input-email")).sendKeys((dataProp.getProperty("InvalidEmail")));

        driver.findElement(By.id("input-password"))
                .sendKeys(prop.getProperty("ValidPassword"));

        driver.findElement(By.xpath("//input[@type='submit']")).click();

        assertWarningMessage();
    }

    @Test(priority = 5)
    public void verifyLoginWithoutCredentials() {

        Objects.requireNonNull(wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("input-email")))).clear();
        driver.findElement(By.id("input-password")).clear();

        driver.findElement(By.xpath("//input[@type='submit']")).click();

        assertWarningMessage();
    }

    private void assertWarningMessage() {
        String actualWarningMessage = Objects.requireNonNull(wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.cssSelector("div.alert.alert-danger.alert-dismissible"))))
                .getText();


        Assert.assertTrue(
                actualWarningMessage.contains(dataProp.getProperty("emailPasswordNoMatchingWarning")),
                "Expected Warning Message is not displayed. Actual: " + actualWarningMessage
        );
    }
}
