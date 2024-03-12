package com.example.droiddesign.UnitTests;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.example.droiddesign.model.OrganizerMessage;

public class OrganizerMessageUnitTest {
    private OrganizerMessage organizerMessage;

    @Before
    public void setUp() {
        // Initialize OrganizerMessage with some default values
        organizerMessage = new OrganizerMessage("2022-01-01", "Hello, World!", "pic123");
    }

    @Test
    public void testConstructor() {
        assertEquals("Date should be initialized correctly", "2022-01-01", organizerMessage.getDate());
        assertEquals("Text should be initialized correctly", "Hello, World!", organizerMessage.getText());
        assertEquals("ImageUploadedPictureID should be initialized correctly", "pic123", organizerMessage.getImageUploadedPictureID());
    }

    @Test
    public void testSettersAndGetters() {
        // Test setDate and getDate
        organizerMessage.setDate("2022-01-02");
        assertEquals("Date should be set correctly", "2022-01-02", organizerMessage.getDate());

        // Test setText and getText
        organizerMessage.setText("New message");
        assertEquals("Text should be set correctly", "New message", organizerMessage.getText());

        // Test setImageUploadedPictureID and getImageUploadedPictureID
        organizerMessage.setImageUploadedPictureID("pic456");
        assertEquals("ImageUploadedPictureID should be set correctly", "pic456", organizerMessage.getImageUploadedPictureID());
    }
}