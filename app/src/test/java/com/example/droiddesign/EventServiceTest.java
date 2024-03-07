package com.example.droiddesign;

import com.example.droiddesign.model.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EventServiceTest {

    private Event event;
    private final String eventId = "1";
    private final String eventName = "Tech Conference";
    private final String eventDate = "2024-03-20";
    private final String eventLocation = "ETLC";
    private final String startTime = "11:00";
    private final String endTime = "11:50";
    private final String geolocation = "35.6895N, 139.6917E";
    private final String organizerOwnerId = "Organizer123";
    private final String imagePosterId = "Poster123";
    private final String description = "A CMPUT 301 lecture.";
    private final Integer signupLimit = 100;
    private final Integer attendeesCount = 0;
    private final String qrCode = "http://example.com/qrcode";

    @BeforeEach
    void setUp() {
        event = new Event(eventId, eventName, eventDate, eventLocation, startTime, endTime, geolocation, organizerOwnerId, imagePosterId, description, signupLimit, attendeesCount, qrCode);
    }

    @Test
    void testEventCreation() {
        assertNotNull(event);
        assertEquals(eventId, event.getEventId());
        assertEquals(eventName, event.getEventName());
        // Continue for the rest of the properties
    }

    @Test
    void testEventNameSetter() {
        String newEventName = "New Tech Conference";
        event.setEventName(newEventName);
        assertEquals(newEventName, event.getEventName());
    }
}