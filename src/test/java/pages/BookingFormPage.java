package pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class BookingFormPage {
    Page page;

    private static final String FULL_NAME_LABEL = "Full Name";
    private static final String CUSTOMER_EMAIL_ID = "#customer-email";
    private static final String PHONE_PLACEHOLDER = "+91 98765 43210";
    private static final String CONFIRM_BTN = "Confirm Booking";

    public BookingFormPage(Page page) {
        this.page = page;
    }
    public void fillBookingForm(String name, String email, String phone) {
        // Fill in the booking form fields
        page.getByLabel(FULL_NAME_LABEL).fill(name);
        page.locator(CUSTOMER_EMAIL_ID).fill(email);
        page.getByPlaceholder(PHONE_PLACEHOLDER).fill(phone);
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(CONFIRM_BTN)).click();
        // #Confirm-booking
        assertThat(page.getByText("Your tickets are reserved.")).isVisible();
    }
}
