package tests;

import com.microsoft.playwright.*;
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.microsoft.playwright.options.AriaRole;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.nio.file.Paths;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class BasicsTest {

    //Invoke browser -> Invoke tab -> type url
    // head mode - launches browser and performs the action
    // headless - does not launch anything and runs in background
    Playwright playwright;
    Browser browser;
    Page page;
    BrowserContext context;

    @BeforeMethod
    public void beforeMethod() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setChannel("chrome").setHeadless(false));

        context = browser.newContext();
        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true)
                .setSources(true));
        page = context.newPage();
//        page = browser.newPage();

        page.setDefaultTimeout(8000);
        page.navigate("https://eventhub.rahulshettyacademy.com/login");
        PlaywrightAssertions.setDefaultAssertionTimeout(7000);
    }

    @AfterMethod(alwaysRun = true)
    public void TearDown() {
        context.tracing().stop
                (new Tracing
                        .StopOptions()
                        .setPath(Paths.get("trace.zip")));
    }

    @Test(description = "Create Event - Book that event and verify if its booked")
    public void DemoTest(){
//        Playwright playwright = Playwright.create();
//        Browser browser = playwright.chromium().launch(); // HEAD MODE
//        Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
//        Browser browser = playwright.webkit().launch(new BrowserType.LaunchOptions().setHeadless(false));
//        Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setChannel("chrome").setHeadless(false));
//
//        Page page = browser.newPage();
//        page.navigate("https://eventhub.rahulshettyacademy.com/login");
        System.out.println(page.title());
        assertThat(page).hasTitle("EventHub — Discover & Book Events");

        //Section 4 - Deep dive into Playwright UI Automation with Assertions & Locator filters
        page.getByLabel("Email").fill("giffydeepu@gmail.com");
//        page.getByPlaceholder("Password").fill("Vvce@2019-23");
        page.getByLabel("Password").fill("Abcd@1234");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign In")).click();

        assertThat(page.getByRole(AriaRole.LINK,
                new Page.GetByRoleOptions().setName("Browse Events →"))).isVisible();

        // Step 1: Create an event from Admin page
        page.navigate("https://eventhub.rahulshettyacademy.com/admin/events");
        //10 Second by default timeout is there for all the actions in playwright. If the element is not found in 10 seconds, it will throw an error.s
        page.locator("#event-title-input").fill("QA Summit Rahul Shetty");
        page.locator("#admin-event-form textarea").fill("Rahul Shetty QA meetups");
        page.getByLabel("Category").selectOption("Concert");
        page.getByLabel("City").fill("Bengaluru");
        page.getByLabel("Venue").fill("Bengaluru International Stadium");
        page.getByLabel("Event Date & Time").fill("2027-06-30T10:00");
        page.getByLabel("Price ($)").fill("100");
        page.getByLabel("Total Seats").fill("50");
        page.locator("#add-event-btn").click();

        //Event created! message should be displayed after creating the event
        assertThat(page.getByText("Event created!")).isVisible(); //5 Second timeout default for assertion. If the element is not found in 5 seconds, it will throw an error.

        page.waitForTimeout(3000);

        // Step 2: Find newly created event in the events page.
        page.locator("#nav-events").click();
        Locator eventCards = page.getByTestId("event-card");
//        System.out.println(eventCards.allInnerTexts());
        System.out.println(eventCards.count());

        Locator targetCard = eventCards.filter(new Locator.FilterOptions().setHasText("QA Summit Rahul Shetty"));
        assertThat(targetCard).isVisible(new LocatorAssertions.IsVisibleOptions().setTimeout(10000));
        String seatsText = targetCard.getByText("seats").innerText();
        System.out.println(seatsText);
        int seatsNumBeforeBooking = Integer.parseInt(seatsText.split(" ")[0]);
        targetCard.getByTestId("book-now-btn").click();

        page.getByLabel("Full Name").fill("Deepak N");
        page.locator("#customer-email").fill("deepak.n@example.com");
        page.getByPlaceholder("+91 98765 43210").fill("+91 98765 43210");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Confirm Booking")).click();
        // #Confirm-booking
        assertThat(page.getByText("Your tickets are reserved.")).isVisible();
        String bookingRef = page.locator(".booking-ref").innerText();
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("View My Bookings")).click();

        // Verify in Booking History
        Locator bookingCards = page.locator("#booking-card");
        Locator targetBookingCard = bookingCards.filter(new Locator.FilterOptions().setHasText(bookingRef));
        assertThat(targetBookingCard).isVisible();

        //Seat Count reduction check
        page.locator("#nav-events").click();
        page.waitForTimeout(3000);
        Locator eventCardsAfterBooking = page.getByTestId("event-card");
        System.out.println(eventCardsAfterBooking.count());

        Locator targetCardAfterBooking = eventCardsAfterBooking.filter(new Locator.FilterOptions().setHasText("QA Summit Rahul Shetty"));
        String seatsTextAfterBooking = targetCardAfterBooking.getByText("seats").innerText();
        System.out.println(seatsTextAfterBooking);

        // Afterbooking < BeforeBooking
        int seatsNumAfterBooking = Integer.parseInt(seatsTextAfterBooking.split(" ")[0]);

        Assert.assertTrue(seatsNumAfterBooking < seatsNumBeforeBooking, "Seat count did not reduce after booking");

    }
}
 