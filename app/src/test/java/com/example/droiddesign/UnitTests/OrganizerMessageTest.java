package com.example.droiddesign.UnitTests;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.example.droiddesign.model.OrganizerMessage;

public class OrganizerMessageTest {

    private OrganizerMessage organizerMessage;
    private final String testDate = "2024-01-01";
    private final String testText = "Welcome to our event!";
    private final String testImageUploadedPictureID = "image123";

    @Before
    public void setUp() {
        organizerMessage = new OrganizerMessage(testDate, testText, testImageUploadedPictureID);
    }

    @Test
    public void constructor_initializesPropertiesCorrectly() {
        assertEquals("Constructor should initialize date correctly", testDate, organizerMessage.getDate());
        assertEquals("Constructor should initialize text correctly", testText, organizerMessage.getText());
        assertEquals("Constructor should initialize imageUploadedPictureID correctly", testImageUploadedPictureID, organizerMessage.getImageUploadedPictureID());
    }

    @Test
    public void setDate_updatesDateCorrectly() {
        String newDate = "2024-02-02";
        organizerMessage.setDate(newDate);
        assertEquals("setDate should update date correctly", newDate, organizerMessage.getDate());
    }

    @Test
    public void setText_updatesTextCorrectly() {
        String newText = "Change of plans!";
        organizerMessage.setText(newText);
        assertEquals("setText should update text correctly", newText, organizerMessage.getText());
    }

    @Test
    public void setImageUploadedPictureID_updatesImageUploadedPictureIDCorrectly() {
        String newImageUploadedPictureID = "image456";
        organizerMessage.setImageUploadedPictureID(newImageUploadedPictureID);
        assertEquals("setImageUploadedPictureID should update imageUploadedPictureID correctly", newImageUploadedPictureID, organizerMessage.getImageUploadedPictureID());
    }
}

