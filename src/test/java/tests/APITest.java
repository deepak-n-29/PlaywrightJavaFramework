package tests;

import com.jayway.jsonpath.JsonPath;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.RequestOptions;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;

public class APITest {

    @Test
    public void e2eApiTest(){
        HashMap<Object, Object> apiData = new HashMap<>();
        apiData.put("email", "giffydeepu@gmail.com");
        apiData.put("password", "Abcd@1234");

        Playwright playwright = Playwright.create();
        APIRequestContext apiRequest = playwright.request().newContext();

        APIResponse loginResponse = apiRequest.post("https://api.eventhub.rahulshettyacademy.com/api/auth/login",
                RequestOptions.create().setData(apiData));

        Assert.assertTrue(loginResponse.ok());
        System.out.println(loginResponse.text());

        String token = JsonPath.read(loginResponse.text(), "$.token");
        System.out.println("Login Success: " + token);

        //Create Event
        String eventTitle = "Playwright API Test Event";
        HashMap<Object, Object> createEventPayload = new HashMap<>();
        createEventPayload.put("title", eventTitle);
        createEventPayload.put("description", "This is a test event for Playwright API testing");
        createEventPayload.put("category", "Conference");
        createEventPayload.put("venue", "Main Road");
        createEventPayload.put("city", "Bangalore");
        createEventPayload.put("eventDate", "2026-10-01T10:00:00.000Z");
        createEventPayload.put("price", 100);
        createEventPayload.put("totalSeats", 100);

        APIResponse eventResponse = apiRequest.post("https://api.eventhub.rahulshettyacademy.com/api/events",
                RequestOptions.create()
                        .setHeader("Authorization","Bearer " + token)
                        .setData(createEventPayload)
                        );
//        System.out.println("Event Created: " + eventResponse.text());

        Assert.assertTrue(eventResponse.ok());
        System.out.println("Event Created: " + eventResponse.text());

        int eventId = JsonPath.read(eventResponse.text(), "$.data.id");
        System.out.println("Event ID: " + eventId);

        // Get Event
        APIResponse retrieveEvents  =apiRequest.get("https://api.eventhub.rahulshettyacademy.com/api/events/",
                RequestOptions.create()
                        .setQueryParam("Page","1")
                        .setQueryParam("Limit","10")
                        .setHeader("Authorization","Bearer " + token)
        );

        Assert.assertTrue(eventResponse.ok(), "Failed to retrieve events");

        List<Integer> allEventIds = JsonPath.read(retrieveEvents.text(), "$.data[*].id");
        System.out.println("All Event IDs: " + allEventIds);

        Assert.assertTrue(allEventIds.contains(eventId), "Created event ID not found in the list of all events");

        // Delete Event
        APIResponse deleteResponse = apiRequest.delete("https://api.eventhub.rahulshettyacademy.com/api/events/" + eventId,
                RequestOptions.create()
                        .setHeader("Authorization","Bearer " + token)
        );
        Assert.assertTrue(deleteResponse.ok(), "Failed to delete event");

        APIResponse verifyRes  =apiRequest.get("https://api.eventhub.rahulshettyacademy.com/api/events/",
                RequestOptions.create()
                        .setQueryParam("Page","1")
                        .setQueryParam("Limit","10")
                        .setHeader("Authorization","Bearer " + token)
        );

        List<String> titlesAfterDeletion = JsonPath.read(verifyRes.text(), "$.data[*].title");
        Assert.assertFalse(titlesAfterDeletion.contains(eventTitle), "Deleted event title still found in the list of all events");
    }
}
