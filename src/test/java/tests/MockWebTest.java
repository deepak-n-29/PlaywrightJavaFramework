package tests;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.nio.file.Paths;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class MockWebTest {
    Playwright playwright;
    Browser browser;
    Page page;
    BrowserContext context;

    @BeforeMethod
    public void Setup() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setChannel("chrome").setHeadless(false));
        page = browser.newPage();
        page.setDefaultTimeout(8000);
        page.navigate("https://eventhub.rahulshettyacademy.com/login");
    }

    @Test(description = "Sandbox banner is shown when 6 events are returned")
    public void DemoTest() {

        page.getByLabel("Email").fill("giffydeepu@gmail.com");
//        page.getByPlaceholder("Password").fill("Vvce@2019-23");
        page.getByLabel("Password").fill("Abcd@1234");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign In")).click();

        assertThat(page.getByRole(AriaRole.LINK,
                new Page.GetByRoleOptions().setName("Browse Events →"))).isVisible();

        // Mock the API response
        page.route("**/api/events**", route -> {
            route.fulfill(
                    new Route.FulfillOptions()
                            .setPath(Paths.get("src/test/resources/events_6.json"))

            );
        });
        page.navigate("https://eventhub.rahulshettyacademy.com/events");
        page.waitForTimeout(5000);

        Locator eventCards = page.getByTestId("event-card");
        assertThat(eventCards.first()).isVisible();

        assertThat(eventCards).hasCount(6);
        assertThat(page.locator(".mx-1").first()).isVisible();

        System.out.println(page.locator(".mx-1").first().allInnerTexts());

        //  Mock the API response with 4 events and verify that the sandbox banner is hidden
        page.route("**/api/events**", route -> {
            route.fulfill(
                    new Route.FulfillOptions()
                            .setPath(Paths.get("src/test/resources/events_4.json"))

            );
        });
        page.navigate("https://eventhub.rahulshettyacademy.com/events");
        page.waitForTimeout(5000);

        Locator eventCards1 = page.getByTestId("event-card");
        assertThat(eventCards1.first()).isVisible();

        Assert.assertEquals(eventCards1.count(), 4);
        assertThat(page.locator(".mx-1").first()).isHidden();

        System.out.println(page.locator(".mx-1").first().allInnerTexts());

    }
        @Test
        public void routeResumeTest() {
            page.getByLabel("Email").fill("giffydeepu@gmail.com");
            page.getByLabel("Password").fill("Abcd@1234");
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign In")).click();

            assertThat(page.getByRole(AriaRole.LINK,
                    new Page.GetByRoleOptions().setName("Browse Events →"))).isVisible();

            // Mock the API Request using route.resume()

            page.getByTestId("nav-bookings").click();
            page.waitForTimeout(5000);

            page.route("**/api/bookings**", route -> route.resume(
                    new Route.ResumeOptions().setUrl("https://eventhub.rahulshettyacademy.com/bookings/137360")
            ));


            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("View Details")).first().click();
            assertThat(page.getByText("Booking not found")).isVisible();
        }


}
