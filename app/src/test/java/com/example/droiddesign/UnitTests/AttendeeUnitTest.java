package com.example.droiddesign.UnitTests;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.example.droiddesign.model.Attendee;
import com.example.droiddesign.model.Event;

import java.util.ArrayList;
import java.util.HashMap;

public class AttendeeUnitTest {
    private Attendee attendee;

    @Before
    public void setUp() {
        // Initialize Attendee with some default values
        attendee = new Attendee("userId123", "attendee");
        attendee.setProfileName("John Doe");
        attendee.setEmail("john.doe@example.com");
        attendee.setPhone("1234567890");
        attendee.setProfilePic("profilePicUrl");
        attendee.setGeolocation(true);
    }

    @Test
    public void testAttendeeConstructor() {
        assertNotNull("Attendee object should not be null", attendee);
    }

    @Test
    public void testGetters() {
        assertEquals("Profile name should match", "John Doe", attendee.getProfileName());
        assertEquals("Email should match", "john.doe@example.com", attendee.getEmail());
        assertEquals("Phone should match", "1234567890", attendee.getPhone());
        assertTrue("Geolocation should be true", attendee.getGeolocation());
    }

    @Test
    public void testToMap() {
        HashMap<String, Object> map = attendee.toMap();

        assertEquals("userId should match", "userId123", map.get("userId"));
        assertEquals("role should match", "attendee", map.get("role"));
        assertEquals("profileName should match", "John Doe", map.get("profileName"));
        assertEquals("email should match", "john.doe@example.com", map.get("email"));
        assertEquals("phone should match", "1234567890", map.get("phone"));
        assertEquals("profilePic should match", "profilePicUrl", map.get("profilePic"));
        assertTrue("eventsList should be an ArrayList", map.get("eventsList") instanceof ArrayList);
    }

    @Test
    public void testAddEvent() {
        // Mock Event object to add to Attendee's event list
        Event mockEvent = new Event() {
            @Override
            public String getEventId() {
                return "eventId123";
            }
        };

        attendee.addEvent(mockEvent);

        assertTrue("eventsList should contain the added event ID", attendee.getEventsList().contains("eventId123"));
    }
}