package tests;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.nio.file.Paths;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class UIValidationsContinuedTest {

    Playwright playwright;
    Browser browser;
    Page page;

    @BeforeMethod(alwaysRun = true)
    public void Setup() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        page = browser.newPage();
        page.navigate("https://rahulshettyacademy.com/AutomationPractice/");
    }

    @Test(groups = {"smoke","regression"})
    public void popupValidation(){
        assertThat(page.getByPlaceholder("Hide/Show Example")).isVisible();
        page.locator("#hide-textbox").click();
        assertThat(page.getByPlaceholder("Hide/Show Example")).isHidden();
        page.onDialog(dialog -> dialog.accept());
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Alert")).click();

        page.waitForTimeout(3000);

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Mouse Hover")).scrollIntoViewIfNeeded();
        page.waitForTimeout(3000);
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Mouse Hover")).hover();
//        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Top")).click();

//        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Learning Paths")).click();
        FrameLocator framesPage = page.frameLocator("#courses-iframe");
        framesPage.getByRole(AriaRole.LINK,
                new FrameLocator.GetByRoleOptions().setName("Learning Paths")).click();
        String textCheck = framesPage.locator(".inner-box h1").textContent();
        System.out.println("Text Check: " + textCheck);
    }

    @Test
    public void screenShotTest(){
        page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("pagescreenshot.png")).setFullPage(true));
        Locator displayedEditBox = page.getByPlaceholder("Hide/Show Example");
        displayedEditBox.screenshot(new Locator.ScreenshotOptions().setPath(Paths.get("editboxscreenshot.png")));
        page.locator("#hide-textbox").click();
        page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("pagePostscreenshot.png")).setFullPage(true));

    }
}


