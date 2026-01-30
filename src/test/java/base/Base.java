package base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import utils.Utilities;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;

public class Base {

    protected WebDriver driver;
    protected Properties prop;
    protected Properties dataProp;

    public void loadPropertiesFile() {

        prop = new Properties();       // environment config
        dataProp = new Properties();   // test data config

        // Load config.properties
        try (InputStream configIS =
                     Base.class.getClassLoader().getResourceAsStream("config.properties")) {

            if (configIS == null) {
                throw new RuntimeException(
                        "config.properties not found on classpath. " +
                                "Ensure it is placed in src/test/resources/config.properties " +
                                "and that the folder is marked as Test Resources Root."
                );
            }

            prop.load(configIS);

        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }

        // Load testdata.properties
        try (InputStream testDataIS =
                     Base.class.getClassLoader().getResourceAsStream("testdata.properties")) {

            if (testDataIS == null) {
                throw new RuntimeException(
                        "testdata.properties not found on classpath. " +
                                "Ensure it is placed in src/test/resources/testData.properties " +
                                "and that the folder is marked as Test Resources Root."
                );
            }

            dataProp.load(testDataIS);

        } catch (IOException e) {
            throw new RuntimeException("Failed to load testdata.properties", e);
        }
    }

    //*****?//

    public WebDriver initializeBrowserAndOpenApplicationURL(String browserName) {

        // Load properties if not loaded yet
        if (prop == null) {
            loadPropertiesFile();
        }

        // Allow browser from config if browserName not provided
        if (browserName == null || browserName.isBlank()) {
            browserName = prop.getProperty("browser", "chrome");
        }

        // Allow url from config
        String url = prop.getProperty("url", "https://tutorialsninja.com/demo/");

        switch (browserName.toLowerCase()) {

            case "chrome":
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--start-maximized");
                options.addArguments("--disable-gpu");
                options.addArguments("--disable-extensions");
                driver = new ChromeDriver(options);
                break;

            case "firefox":
                driver = new FirefoxDriver();
                driver.manage().window().maximize();
                break;

            case "edge":
                driver = new EdgeDriver();
                driver.manage().window().maximize();
                break;

            default:
                throw new IllegalArgumentException(
                        "Unsupported browser: " + browserName + " (use chrome, firefox, or edge)"
                );
        }

        driver.manage().deleteAllCookies();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(Utilities.IMPLICIT_WAIT_TIME));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(Utilities.PAGE_LOAD_TIME));

        driver.get(url);

        return driver;
    }
}
