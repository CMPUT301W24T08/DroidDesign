package com.example.droiddesign.UnitTests;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.example.droiddesign.model.Admin;

import java.util.HashMap;

public class AdminUnitTest {
    private Admin admin;

    @Before
    public void setUp() {
        // Initialize your Admin object here
        admin = new Admin("adminId123", "admin");
    }

    @Test
    public void testToMap() {
        // Call the toMap method
        HashMap<String, Object> adminMap = admin.toMap();

        // Check if the map is not null
        assertNotNull("The map should not be null", adminMap);

        // Check if the map contains the correct userId and role
        assertEquals("The userId does not match", "adminId123", adminMap.get("userId"));
        assertEquals("The role does not match", "admin", adminMap.get("role"));
    }
}