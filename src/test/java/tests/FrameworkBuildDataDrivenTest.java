package tests;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.*;
import utils.DataProviderUtil;

import java.io.IOException;
import java.util.HashMap;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class FrameworkBuildDataDrivenTest extends TestBase {

    @DataProvider(name = "eventBookingData")
    public Object[][] eventBookingData() throws IOException {
        return DataProviderUtil.getJsonToMap("/src/test/resources/eventBookingData.json");
    }

    @Test(groups = {"framework"}, dataProvider = "eventBookingData", description = "Create Event - Book that event and verify if its booked")
    public void DemoTest(HashMap<String, String> bookingData) {

        LoginPage loginPage = new LoginPage(page, baseUrl);
        DashboardPage dashboardPage = loginPage.loginToApplication();
        dashboardPage.waitForDashboardToLoad();

        AdminEventsPage adminEventsPage = new AdminEventsPage(page);
        adminEventsPage.goTo();
        adminEventsPage.createEvent(
                bookingData.get("titlePrefix"),
                bookingData.get("description"),
                bookingData.get("city"),
                bookingData.get("venue"),
                bookingData.get("dateTime"),
                bookingData.get("price"),
                bookingData.get("totalSeats")
        );
        page.waitForTimeout(100);

        // Step 2: Find newly created event in the events page.
        EventsPage eventsPage = new EventsPage(page);
        eventsPage.goTo();

        Locator targetCard = eventsPage.findEventCard(bookingData.get("titlePrefix"));
        int seatsNumBeforeBooking = eventsPage.getSeatsCount(targetCard);
        BookingFormPage bookingFormPage = eventsPage.proceedToBooking(targetCard);


        bookingFormPage.fillBookingForm(
                bookingData.get("fullName"),
                bookingData.get("email"),
                bookingData.get("phone"));

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

        Locator targetCardAfterBooking = eventCardsAfterBooking.filter(new Locator.FilterOptions().setHasText(bookingData.get("titlePrefix")));
        String seatsTextAfterBooking = targetCardAfterBooking.getByText("seats").innerText();
        System.out.println(seatsTextAfterBooking);

        // Afterbooking < BeforeBooking
        int seatsNumAfterBooking = Integer.parseInt(seatsTextAfterBooking.split(" ")[0]);

        Assert.assertTrue(seatsNumAfterBooking < seatsNumBeforeBooking, "Seat count did not reduce after booking");

    }
}
 