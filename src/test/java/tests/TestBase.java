package tests;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import org.testng.annotations.BeforeMethod;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class TestBase {
    Playwright playwright;
    Browser browser;
    Page page;
    String baseUrl;

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod() throws IOException {
        Properties prop = new Properties();
        FileInputStream fis = new FileInputStream("src/test/resources/config.properties");
        prop.load(fis);

        //mvn test -PRegresssion -Dbrowser=chrome
        //mvn test -PRegresssion -Dbrowser=chrome -Denv=dev
        String browserName = System.getProperty("browser")!=null ? System.getProperty("browser") : prop.getProperty("browser");
//        String browserName = prop.getProperty("browser");
        String env = System.getProperty("env")!=null ? System.getProperty("env") : prop.getProperty("env");

        playwright = Playwright.create();

        if("firefox".equalsIgnoreCase(browserName)){
//            browser = playwright.firefox().launch(new BrowserType.LaunchOptions().setHeadless(false));
            browser = playwright.webkit().launch();

        } else if("safari".equalsIgnoreCase(browserName)){
//            browser = playwright.webkit().launch(new BrowserType.LaunchOptions().setHeadless(false));
            browser = playwright.webkit().launch();

        } else {
//            browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setChannel("chrome").setHeadless(false));
            browser = playwright.chromium().launch();
        }

        page = browser.newPage();
        page.setDefaultTimeout(8000);

//        baseUrl = prop.getProperty("qa.baseUrl");
        baseUrl = prop.getProperty(env +".baseUrl");

        PlaywrightAssertions.setDefaultAssertionTimeout(10000);
    }
}
