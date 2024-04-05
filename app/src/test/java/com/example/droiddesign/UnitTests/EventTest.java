package com.example.droiddesign.UnitTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.example.droiddesign.model.Event;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class EventTest {

    @Mock
    private Event mockedEvent;

    @Test
    public void testEventConstructor() {
        // Create test data
        String eventId = "123";
        String eventName = "Test Event";
        String eventDate = "2022-01-01";
        String eventLocation = "Test Location";
        String startTime = "10:00 AM";
        String endTime = "12:00 PM";
        String geolocation = "1.234567, -1.234567";
        String organizerOwnerId = "456";
        String imagePosterId = "789";
        String description = "Test description";
        int signupLimit = 100;
        int attendeesCount = 50;
        int milestones = 5;
        String shareQrCode = "shareQrCode";
        String checkInQrCode = "checkInQrCode";
        String shareQrId = "shareQrId";
        String checkInQrId = "checkInQrId";

        // Create the event object using the constructor
        Event event = new Event(eventId, eventName, eventDate, eventLocation, startTime, endTime, geolocation,
                organizerOwnerId, imagePosterId, description, signupLimit, attendeesCount, milestones,
                shareQrCode, checkInQrCode, shareQrId, checkInQrId);

        // Verify that the event object has been created with the expected values
        assertEquals(eventId, event.getEventId());
        assertEquals(eventName, event.getEventName());
        assertEquals(eventDate, event.getEventDate());
        assertEquals(eventLocation, event.getEventLocation());
        assertEquals(startTime, event.getStartTime());
        assertEquals(endTime, event.getEndTime());
        assertEquals(geolocation, event.getGeolocation());
        assertEquals(organizerOwnerId, event.getOrganizerOwnerId());
        assertEquals(imagePosterId, event.getImagePosterId());
        assertEquals(description, event.getDescription());
        assertEquals(shareQrCode, event.getShareQrCode());
        assertEquals(checkInQrCode, event.getCheckInQrCode());
        assertEquals(shareQrId, event.getShareQrId());
        assertEquals(checkInQrId, event.getCheckInQrId());
    }

    @Mock
    private FirebaseFirestore mockedFirestore;

    private Event event;

    @Before
    public void setUp() {
        // Assuming Event class has a constructor or method to set Firestore instance
        event = new Event(); // Assuming a constructor without Firestore instance for simplicity

    }

    @Test
    public void constructorTest() {
        assertNotNull("Event instance should not be null", event);
    }

    @Test
    public void settingAndGettingEventName() {
        String expectedName = "Sample Event";
        event.setEventName(expectedName);
        assertEquals("Event name should match the set value", expectedName, event.getEventName());
    }

    @Test
    public void eventUpdatesCorrectly() {
        String newLocation = "New Location";
        event.setEventLocation(newLocation);
        assertEquals("Event location should be updated", newLocation, event.getEventLocation());
    }


}
