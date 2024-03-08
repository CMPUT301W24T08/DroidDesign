package com.example.droiddesign.UnitTests;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.example.droiddesign.model.Event;
import com.example.droiddesign.model.Organizer;

import java.util.ArrayList;
import java.util.HashMap;

public class OrganizerUnitTest {
    private Organizer organizer;

    @Before
    public void setUp() {
        // Initialize Organizer with some default values
        organizer = new Organizer("userId456", "organizer", "organizer@example.com");
        organizer.setUsername("OrganizerName");
        organizer.setPhone("0987654321");
        organizer.setProfilePic("organizerProfilePicUrl");
        organizer.setGeolocation(true);
    }

    @Test
    public void testOrganizerConstructor() {
        assertNotNull("Organizer object should not be null", organizer);
    }

    @Test
    public void testToMap() {
        HashMap<String, Object> map = organizer.toMap();

        assertEquals("userId should match", "userId456", map.get("userId"));
        assertEquals("role should match", "organizer", map.get("role"));
        assertEquals("email should match", "organizer@example.com", map.get("email"));
        assertEquals("username should match", "OrganizerName", map.get("username"));
        assertEquals("phone should match", "0987654321", map.get("phone"));
        assertEquals("profilePic should match", "organizerProfilePicUrl", map.get("profilePic"));
        assertTrue("geolocation should be true", (Boolean) map.get("geolocation"));
        assertTrue("eventsList should be an ArrayList", map.get("eventsList") instanceof ArrayList);
    }

    @Test
    public void testAddEvent() {
        // Mock Event object to add to Organizer's event list
        Event mockEvent = new Event() {
            @Override
            public String getEventId() {
                return "event456Id";
            }
        };

        organizer.addEvent(mockEvent);

        assertTrue("eventsIdList should contain the added event ID", organizer.getEventsIdList().contains("event456Id"));
    }
}
