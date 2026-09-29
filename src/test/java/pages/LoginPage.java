package pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class LoginPage {
    Page page;
    String baseUrl;

    private static final   String email_placeholder = "you@email.com";
    private static final String password_label = "Password";

    // private - means this variable can only be accessed within this class. It cannot be accessed from outside the class.
    // static - variable is shared among all instances of the class. It belongs to the class itself, rather than any specific instance. No extra memory is allocated for each instance of the class. It is stored in the static memory area, which is shared among all instances of the class.
    // final - variable cannot be reassigned after it has been initialized. It is a constant value that cannot be changed once it has been set. It is a good practice to use final for variables that should not be modified, as it helps to prevent accidental changes to the variable's value.

    public LoginPage(Page page, String baseUrl){
        this.page = page;
        this.baseUrl = baseUrl;
    }
    public DashboardPage loginToApplication() {
        page.navigate(baseUrl);

        System.out.println(page.title());
        assertThat(page).hasTitle("EventHub — Discover & Book Events");

        page.getByPlaceholder(email_placeholder).fill("giffydeepu@gmail.com");
        page.getByLabel(password_label).fill("Abcd@1234");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign In")).click();

        DashboardPage dashboardPage = new DashboardPage(page);
        return dashboardPage;
    }
}
