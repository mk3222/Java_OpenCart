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

public class Register extends Base {

    private WebDriverWait wait;

    // TestNG needs a no-arg constructor (do not define WebDriver constructors here)

    @BeforeMethod
    public void setup() {

        // Use Base class driver (do NOT redeclare WebDriver driver in this class)
        driver = initializeBrowserAndOpenApplicationURL("chrome");
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // Navigate to Register page
        Objects.requireNonNull(wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//span[normalize-space()='My Account']")))).click();

        Objects.requireNonNull(wait.until(ExpectedConditions.elementToBeClickable(
                By.linkText("Register")))).click();
    }

    @AfterMethod
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test(priority = 1)
    public void verifyRegisteringAnAccountWithMandatoryFields() {

        Objects.requireNonNull(wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("input-firstname"))))
                .sendKeys("John");

        driver.findElement(By.id("input-lastname")).sendKeys("Smith");

        driver.findElement(By.id("input-email"))
                .sendKeys(Utilities.generateEmailWithTimeStamp());

        driver.findElement(By.id("input-telephone")).sendKeys("555-555-5555");

        driver.findElement(By.id("input-password")).sendKeys("123456");
        driver.findElement(By.id("input-confirm")).sendKeys("123456");

        driver.findElement(By.name("agree")).click();
        driver.findElement(By.xpath("//input[@value='Continue']")).click();

        WebElement successHeading = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#content h1")));

        Assert.assertNotNull(successHeading);
        Assert.assertEquals(
                successHeading.getText().trim(),
                "Your Account Has Been Created!",
                "Account creation success heading is incorrect."
        );

        WebElement successParagraph = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#content p")));

        Assert.assertNotNull(successParagraph);
        Assert.assertTrue(
                successParagraph.getText().contains("successfully created"),
                "Success confirmation message is not displayed correctly. Actual: " + successParagraph.getText()
        );
    }

    @Test(priority = 2)
    public void verifyRegisteringAnAccountWithExistingEmailAddress() {

        Objects.requireNonNull(wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("input-firstname"))))
                .sendKeys("John");

        driver.findElement(By.id("input-lastname")).sendKeys("Smith");

        driver.findElement(By.id("input-email")).sendKeys("js@a.com");

        driver.findElement(By.id("input-telephone")).sendKeys("555-555-5555");

        driver.findElement(By.id("input-password")).sendKeys("123456");
        driver.findElement(By.id("input-confirm")).sendKeys("123456");

        driver.findElement(By.name("agree")).click();
        driver.findElement(By.xpath("//input[@value='Continue']")).click();

        assertExistingEmailWarning();
    }

    @Test(priority = 3)
    public void verifyRegisteringAnAccountWithoutFillingAnyDetails() {

        // Click Continue without filling anything and without agreeing
        Objects.requireNonNull(wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//input[@value='Continue']")))).click();

        // Top privacy policy warning
        String actualAlert = Objects.requireNonNull(wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("div.alert.alert-danger.alert-dismissible")))).getText();

        Assert.assertTrue(
                actualAlert.contains("Warning: You must agree to the Privacy Policy!"),
                "Privacy Policy warning is not displayed. Actual: " + actualAlert
        );

        // Field warnings
        Assert.assertTrue(
                driver.findElement(By.xpath("//input[@id='input-firstname']/following-sibling::div[@class='text-danger']"))
                        .getText()
                        .contains("First Name must be between 1 and 32 characters!"),
                "First Name warning message is not displayed!"
        );

        Assert.assertTrue(
                driver.findElement(By.xpath("//input[@id='input-lastname']/following-sibling::div[@class='text-danger']"))
                        .getText()
                        .contains("Last Name must be between 1 and 32 characters!"),
                "Last Name warning message is not displayed!"
        );

        Assert.assertTrue(
                driver.findElement(By.xpath("//input[@id='input-email']/following-sibling::div[@class='text-danger']"))
                        .getText()
                        .contains("E-Mail Address does not appear to be valid!"),
                "Email warning message is not displayed!"
        );

        Assert.assertTrue(
                driver.findElement(By.xpath("//input[@id='input-telephone']/following-sibling::div[@class='text-danger']"))
                        .getText()
                        .contains("Telephone must be between 3 and 32 characters!"),
                "Telephone warning message is not displayed!"
        );

        Assert.assertTrue(
                driver.findElement(By.xpath("//input[@id='input-password']/following-sibling::div[@class='text-danger']"))
                        .getText()
                        .contains("Password must be between 4 and 20 characters!"),
                "Password warning message is not displayed!"
        );
    }

    private void assertExistingEmailWarning() {
        String actualWarningMessage = Objects.requireNonNull(wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.cssSelector("div.alert.alert-danger.alert-dismissible"))))
                .getText();

        String expectedWarningMessage = "Warning: E-Mail Address is already registered!";

        Assert.assertTrue(
                actualWarningMessage.contains(expectedWarningMessage),
                "Expected warning is not displayed. Actual: " + actualWarningMessage
        );
    }
}
