package com.example.droiddesign.UnitTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.example.droiddesign.model.Event;
import com.example.droiddesign.model.User;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

public class UserUnitTest {
    private User user;

    @Before
    public void setUp() {
        // Initialize User with some default values
        user = new User("userId123", "user");
        user.setProfileName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setPhone("1234567890");
        user.setProfilePic("profilePicUrl");
        user.setGeolocation(true);
    }

    @Test
    public void testUserConstructor() {
        assertNotNull("User object should not be null", user);
    }

    @Test
    public void testGetters() {
        assertEquals("Profile name should match", "John Doe", user.getProfileName());
        assertEquals("Email should match", "john.doe@example.com", user.getEmail());
        assertEquals("Phone should match", "1234567890", user.getPhone());
        assertTrue("Geolocation should be true", user.getGeolocation());
    }

    @Test
    public void testToMap() {
        HashMap<String, Object> map = user.toMap();

        assertEquals("userId should match", "userId123", map.get("userId"));
        assertEquals("role should match", "user", map.get("role"));
        assertEquals("profileName should match", "John Doe", map.get("profileName"));
        assertEquals("email should match", "john.doe@example.com", map.get("email"));
        assertEquals("phone should match", "1234567890", map.get("phone"));
        assertEquals("profilePic should match", "profilePicUrl", map.get("profilePic"));
        assertTrue("signedEventsList should be an ArrayList", map.get("signedEventsList") instanceof ArrayList);
        assertTrue("managedEventsList should be an ArrayList", map.get("managedEventsList") instanceof ArrayList);
    }

    @Test
    public void testAddEvent() {
        // Mock Event object to add to User's event list
        Event mockEvent = new Event() {
            @Override
            public String getEventId() {
                return "eventId123";
            }
        };

        user.addSignedEvent(mockEvent);

	    assertTrue("eventsList should contain the added event ID", user.getSignedEventsList().contains("eventId123"));
    }
}