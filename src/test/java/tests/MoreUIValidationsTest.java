package tests;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.nio.file.Paths;

public class MoreUIValidationsTest {
    Playwright playwright;
    Browser browser;
    Page page;
    BrowserContext context;

    @BeforeMethod(alwaysRun = true)
    public void Setup() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        context = browser.newContext();
        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true)
                .setSources(true));
        page = context.newPage();
        page.navigate("https://rahulshettyacademy.com/loginpagePractise/");
    }

    @AfterMethod(alwaysRun = true)
    public void TearDown() {
        context.tracing().stop
                (new Tracing
                        .StopOptions()
                        .setPath(Paths.get("trace.zip")));
    }

    @Test
    public void ChildWindowHandle(){
        Locator blinkingTexts = page.locator(".blinkingText");
        Page newPage = context.waitForPage(() -> blinkingTexts.first().click());
        newPage.waitForLoadState();
        String childText = newPage.locator(".red").textContent();
        //        Please email us at mentor@rahulshettyacademy.com with below template to receive response
        String emailId = childText.split("at ")[1].split(" ")[0];
        System.out.println("Email Id: " + emailId);
        page.getByLabel("Username").fill(emailId);

        System.out.println(page.getByLabel("Username").inputValue());
        page.waitForTimeout(3000);
    }

    @Test(groups = {"smoke"})
    public void UIControls(){
        Locator userRdBtn = page.getByRole(AriaRole.RADIO, new Page.GetByRoleOptions().setName("User"));
        userRdBtn.click();
        page.locator("#okayBtn").click();
//        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Okay")).click();
        Assert.assertTrue(userRdBtn.isChecked());

        Locator checkBoxTerms = page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("I agree to the terms and conditions"));
        checkBoxTerms.check();
        Assert.assertTrue(checkBoxTerms.isChecked());

        page.getByRole(AriaRole.COMBOBOX).selectOption("Teacher");

        page.waitForTimeout(3000);

    }


}
