package testcases;

import base.Base;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.Objects;

public class Search extends Base {

    private WebDriverWait wait;

    @BeforeMethod
    public void setup() {

        // Use Base class driver
        driver = initializeBrowserAndOpenApplicationURL("chrome");
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @AfterMethod
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test(priority = 1)
    public void verifySearchWithValidProduct() {

        Objects.requireNonNull(wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[@id='search']/input"))))
                .sendKeys("HP");

        driver.findElement(By.cssSelector("button.btn.btn-default.btn-lg")).click();

        Assert.assertTrue(
                Objects.requireNonNull(wait.until(ExpectedConditions.visibilityOfElementLocated(
                                By.linkText("HP LP3065"))))
                        .isDisplayed(),
                "Expected product is not displayed in search results."
        );
    }


    @Test(priority = 2)
    public void verifySearchWithInvalidProduct() {

        Objects.requireNonNull(wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='search']/input"))))
                .clear();

        driver.findElement(By.xpath("//*[@id='search']/input")).sendKeys("Honda");

        driver.findElement(By.cssSelector("button.btn.btn-default.btn-lg")).click();

        String actualSearchMessage = Objects.requireNonNull(wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.xpath("//p[normalize-space()='There is no product that matches the search criteria.']"))))
                .getText();

        Assert.assertEquals(
                actualSearchMessage.trim(),
                "There is no product that matches the search criteria.",
                "No product message in search results is not displayed."
        );
    }


    @Test(priority = 3)
    public void verifySearchWithoutAnyProduct() {

        Objects.requireNonNull(wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='search']/input"))))
                .clear();

        driver.findElement(By.xpath("//*[@id='search']/input")).sendKeys("");

        driver.findElement(By.cssSelector("button.btn.btn-default.btn-lg")).click();

        String actualSearchMessage = Objects.requireNonNull(wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.xpath("//p[normalize-space()='There is no product that matches the search criteria.']"))))
                .getText();

        Assert.assertEquals(
                actualSearchMessage.trim(),
                "There is no product that matches the search criteria.",
                "No product message in search results is not displayed."
        );
    }

}
