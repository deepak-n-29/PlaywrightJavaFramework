package tests;

import com.microsoft.playwright.*;
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.microsoft.playwright.options.AriaRole;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.*;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class FrameworkBuildTest extends TestBase {


    @Test(groups = {"framework"}, description = "Create Event - Book that event and verify if its booked")
    public void DemoTest(){
        String eventTitle = "QA Summit Rahul Shetty";

        LoginPage loginPage = new LoginPage(page, baseUrl);
        DashboardPage dashboardPage = loginPage.loginToApplication();
        dashboardPage.waitForDashboardToLoad();

        AdminEventsPage adminEventsPage = new AdminEventsPage(page);
        adminEventsPage.goTo();
        adminEventsPage.createEvent(
                         eventTitle,
                        "Automation Testing Conference",
                        "Bangalore", "ITC Gardenia", "2026-11-15T10:00", "100", "50");



        page.waitForTimeout(3000);

        // Step 2: Find newly created event in the events page.
        EventsPage eventsPage = new EventsPage(page);
        eventsPage.goTo();

        Locator targetCard = eventsPage.findEventCard(eventTitle);
        int seatsNumBeforeBooking = eventsPage.getSeatsCount(targetCard);
        BookingFormPage bookingFormPage = eventsPage.proceedToBooking(targetCard);


        bookingFormPage.fillBookingForm("Deepak N", "deepak.n@example.com", "+91 98765 43210");
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
 