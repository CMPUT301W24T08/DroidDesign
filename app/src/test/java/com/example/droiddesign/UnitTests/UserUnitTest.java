package com.example.droiddesign.UnitTests;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.example.droiddesign.model.User;

import java.util.HashMap;

public class UserUnitTest {
    private User user;

    @Before
    public void setUp() {
        // Initialize a concrete User class here. Since User is abstract, you need a concrete subclass for testing.
        user = new User("userID123", "attendee") {
            @Override
            public HashMap<String, Object> toMap() {
                // Implementation for testing purposes
                HashMap<String, Object> map = new HashMap<>();
                map.put("userId", getUserId());
                map.put("role", getRole());
                return map;
            }
        };
    }

    @Test
    public void testUserConstructorAndGetterMethods() {
        assertEquals("userID123", user.getUserId());
        assertEquals("attendee", user.getRole());
    }

    @Test
    public void testToMap() {
        // Assuming toMap is correctly implemented in the anonymous subclass
        HashMap<String, Object> map = user.toMap();

        assertNotNull("Map should not be null", map);
        assertEquals("Map userId should match the user's userId", "userID123", map.get("userId"));
        assertEquals("Map role should match the user's role", "attendee", map.get("role"));
    }
}