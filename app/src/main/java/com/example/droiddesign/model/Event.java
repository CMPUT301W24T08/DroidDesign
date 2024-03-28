package com.example.droiddesign.model;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Represents an event within the system, holding all necessary details related to the event.
 * This class includes information about the event's schedule, location, associated organizer, and more.
 */
public class Event {

    /**
     * Unique identifier for the event.
     */
    private String eventId;

    /**
     * Name of the event.
     */
    private String eventName;

    /**
     * Location where the event is held.
     */
    private String eventLocation;

    /**
     * Date of the event.
     */
    private String eventDate;

    /**
     * Start time of the event.
     */
    private String startTime;

    /**
     * End time of the event.
     */
    private String endTime;

    /**
     * Date in a specific string format.
     */
    private String date;

    /**
     * Geolocation data for the event, typically representing latitude and longitude.
     */
    private String geolocation;

    /**
     * User ID of the organizer who owns the event.
     */
    private String organizerOwnerId;

    private HashMap<String, Integer> checkedInUsers = new HashMap<>();


    /**
     * Identifier for the image poster associated with the event.
     */
    private String imagePosterId;

    /**
     * Description of the event.
     */
    private String description;

    /**
     * Limit on the number of signups allowed for the event.
     */
    private Integer signupLimit;

    /**
     * Count of current attendees signed up for the event.
     */
    private Integer attendeesCount;
    /**
     * List of milestone attendance numbers for the event
     */
    private Integer milestones;

    /**
     * QR code data associated with the event for easy sharing and identification.
     */
    private String qrCode;

    /**
     * List of user IDs for attendees who have signed up for the event.
     */
    private List<String> attendeeList;

    /**
     * List of messages from the organizer associated with the event.
     */
    private List<OrganizerMessage> organizerMessages;

    /**
     * Static path for the Firestore collection where event data is stored.
     */
    private static final String COLLECTION_PATH = "EventsDB";

    /**
     * Firestore reference to this specific event, used transiently and not stored in the database.
     */
    private transient DocumentReference eventRef;

    /**
     * Default constructor used for data retrieval from Firestore.
     */


    public Event() {}


    /**
     * Constructs an Event instance with detailed attributes specifying the event's properties.
     *
     * @param eventId The unique identifier for the event.
     * @param eventName The name of the event.
     * @param eventDate The date when the event is scheduled.
     * @param eventLocation The location where the event will take place.
     * @param startTime The start time of the event.
     * @param endTime The end time of the event.
     * @param geolocation The geolocation data for the event.
     * @param organizerOwnerId The ID of the user who organized the event.
     * @param imagePosterId The ID for the image poster of the event.
     * @param description A description of the event.
     * @param signupLimit The maximum number of attendees allowed for the event.
     * @param attendeesCount The current number of attendees signed up for the event.
     * @param qrCode The QR code data associated with the event.
     */
    public Event(String eventId, String eventName, String eventDate, String eventLocation, String startTime, String endTime, String geolocation, String organizerOwnerId, String imagePosterId, String description, Integer signupLimit, Integer attendeesCount, String qrCode) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.eventLocation = eventLocation;
        this.startTime = startTime;
        this.endTime = endTime;
        this.geolocation = geolocation;
        this.organizerOwnerId = organizerOwnerId;
        this.imagePosterId = imagePosterId;
        this.description = description;
        this.signupLimit = signupLimit;
        this.attendeesCount = attendeesCount;
        this.qrCode = qrCode;
    }

// Getters and setters with JavaDoc comments

    /**
     * Gets the event location.
     *
     * @return The location of the event.
     */
    public String getEventLocation() {
        return eventLocation;
    }

    /**
     * Sets the event location and updates the corresponding field in Firestore.
     *
     * @param eventLocation The new location of the event.
     */
    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
        updateFirestore("eventLocation", eventLocation);
    }

    /**
     * Gets the event ID.
     *
     * @return The ID of the event.
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * Sets the event ID and updates the corresponding field in Firestore.
     *
     * @param eventId The new ID of the event.
     */
    public void setEventId(String eventId) {
        this.eventId = eventId;
        updateFirestore("eventId", eventId);
    }



    /**
     * Retrieves the name of the event.
     *
     * @return The name of the event.
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * Sets the name of the event and updates the corresponding field in Firestore.
     *
     * @param eventName The new name of the event.
     */
    public void setEventName(String eventName) {
        this.eventName = eventName;
        updateFirestore("eventName", eventName);
    }

    /**
     * Retrieves the scheduled date of the event.
     *
     * @return The scheduled date of the event.
     */
    public String getEventDate() {
        return eventDate;
    }

    /**
     * Sets the scheduled date of the event and updates the corresponding field in Firestore.
     *
     * @param eventDate The new date of the event.
     */
    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
        updateFirestore("eventDate", eventDate);
    }

    /**
     * Retrieves the start time of the event.
     *
     * @return The start time of the event.
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * Sets the start time of the event and updates the corresponding field in Firestore.
     *
     * @param startTime The new start time for the event.
     */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
        updateFirestore("startTime", startTime);
    }

    /**
     * Retrieves the end time of the event.
     *
     * @return The end time of the event.
     */
    public String getEndTime() {
        return endTime;
    }

    /**
     * Sets the end time of the event and updates the corresponding field in Firestore.
     *
     * @param endTime The new end time for the event.
     */
    public void setEndTime(String endTime) {
        this.endTime = endTime;
        updateFirestore("endTime", endTime);
    }

    /**
     * Retrieves the formatted date of the event.
     *
     * @return The formatted date string of the event.
     */
    public String getDate() {
        return date;
    }

    /**
     * Sets the formatted date of the event and updates the corresponding field in Firestore.
     *
     * @param date The new formatted date for the event.
     */
    public void setDate(String date) {
        this.date = date;
        updateFirestore("date", date);
    }

    /**
     * Retrieves the geolocation of the event.
     *
     * @return The geolocation string of the event.
     */
    public String getGeolocation() {
        return geolocation;
    }

    /**
     * Sets the geolocation of the event and updates the corresponding field in Firestore.
     *
     * @param geolocation The new geolocation for the event.
     */
    public void setGeolocation(String geolocation) {
        this.geolocation = geolocation;
        updateFirestore("geolocation", geolocation);
    }

    /**
     * Retrieves the organizer's user ID for the event.
     *
     * @return The organizer's user ID.
     */
    public String getOrganizerOwnerId() {
        return organizerOwnerId;
    }

    /**
     * Sets the organizer's user ID for the event and updates the corresponding field in Firestore.
     *
     * @param organizerOwnerId The new organizer's user ID for the event.
     */
    public void setOrganizerOwnerId(String organizerOwnerId) {
        this.organizerOwnerId = organizerOwnerId;
        updateFirestore("organizerOwnerId", organizerOwnerId);
    }

    /**
     * Retrieves the image poster ID for the event.
     *
     * @return The image poster ID.
     */
    public String getImagePosterId() {
        return imagePosterId;
    }

    /**
     * Sets the image poster ID for the event and updates the corresponding field in Firestore.
     *
     * @param imagePosterId The new image poster ID for the event.
     */
    public void setImagePosterId(String imagePosterId) {
        this.imagePosterId = imagePosterId;
        updateFirestore("imagePosterId", imagePosterId);
    }

    /**
     * Retrieves the description of the event.
     *
     * @return The event description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the event and updates the corresponding field in Firestore.
     *
     * @param description The new description for the event.
     */
    public void setDescription(String description) {
        this.description = description;
        updateFirestore("description", description);
    }

    /**
     * Retrieves the signup limit for the event.
     *
     * @return The signup limit.
     */
    public Integer getSignupLimit() {
        return signupLimit;
    }

    /**
     * Sets the signup limit for the event and updates the corresponding field in Firestore.
     *
     * @param signupLimit The new signup limit for the event.
     */
    public void setSignupLimit(Integer signupLimit) {
        this.signupLimit = signupLimit;
        updateFirestore("signupLimit", signupLimit);
    }


    /**
     * Retrieves the count of attendees that have signed up for the event.
     *
     * @return The count of attendees.
     */
    public Integer getAttendeesCount() {
        return attendeesCount;
    }

    /**
     * Retrieves the list of event attendance milestones.
     * @return
     */
    public Integer getMilestones() {
        return milestones;
    }

    public void setMilestones(Integer milestone) {
        this.milestones = milestone;
    }

    /**
     * Sets the count of attendees for the event and updates the corresponding field in Firestore.
     *
     * @param attendeesCount The new attendees count.
     */
    public void setAttendeesCount(Integer attendeesCount) {
        this.attendeesCount = attendeesCount;
        updateFirestore("attendeesCount", attendeesCount);
    }

    /**
     * Retrieves the QR code associated with the event.
     *
     * @return The QR code string.
     */
    public String getQrCode() {
        return qrCode;
    }

    /**
     * Sets the QR code for the event and updates the corresponding field in Firestore.
     *
     * @param qrCode The new QR code for the event.
     */
    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
        updateFirestore("qrCode", qrCode);
    }

    /**
     * Retrieves the list of attendee IDs for the event.
     *
     * @return The list of attendee IDs.
     */
    public List<String> getAttendeeList() {
        return attendeeList;
    }

    /**
     * Sets the list of attendee IDs for the event and updates the corresponding field in Firestore.
     *
     * @param attendeeList The new list of attendee IDs.
     */
    public void setAttendeeList(List<String> attendeeList) {
        this.attendeeList = attendeeList;
        updateFirestore("attendeeList", attendeeList);
    }

    /**
     * Retrieves the list of messages from the organizer associated with the event.
     *
     * @return The list of organizer messages.
     */
    public List<OrganizerMessage> getOrganizerMessages() {
        return organizerMessages;
    }

    /**
     * Sets the list of organizer messages for the event and updates the corresponding field in Firestore.
     *
     * @param organizerMessages The new list of organizer messages.
     */
    public void setOrganizerMessages(List<OrganizerMessage> organizerMessages) {
        this.organizerMessages = organizerMessages;
        updateFirestore("organizerMessages", organizerMessages);
    }

    /**
     * Generates a random hash ID.
     *
     * @return A randomly generated unique identifier.
     */
    public String getHashId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Saves the current state of the event object to Firestore, merging it with existing data.
     */
    public void saveToFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> eventData = this.toMap();
        db.collection(COLLECTION_PATH).document(this.eventId).set(eventData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    // Logic here for what to do if the event is successfully saved.
                })
                .addOnFailureListener(e -> {
                    // Logic here for what to do if there is a failure in saving the event.
                });
    }


    /**
     * Updates a single field of the current event object in Firestore.
     *
     * @param fieldName The name of the field to update.
     * @param value The new value to set for the specified field.
     */
    private void updateFirestore(String fieldName, Object value) {
        if (this.eventId != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Map<String, Object> updateData = new HashMap<>();
            updateData.put(fieldName, value);
            db.collection(COLLECTION_PATH).document(this.eventId).set(updateData, SetOptions.merge());
        }
    }

    /**
     * Updates multiple fields of an event in Firestore.
     *
     * This method takes a map of field names and their new values and updates these fields in the Firestore document corresponding to this event.
     * If the update is successful, a confirmation message is displayed to the user. In case of failure, an error message is shown.
     *
     * @param updatedFields A map containing the field names as keys and their new values as the map values.
     */
    public void updateEventInFirestore(Map<String, Object> updatedFields) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (this.eventId != null && !updatedFields.isEmpty()) {
            db.collection(COLLECTION_PATH).document(this.eventId).update(updatedFields)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("UpdateEvent", "Event successfully updated in Firestore.");
                    })
                    .addOnFailureListener(e -> {
                        Log.e("UpdateEvent", "Error updating event in Firestore.", e);
                    });
        } else {
            Log.e("UpdateEvent", "Event ID is null or fields to update are empty.");
        }
    }

    public void checkInUser(String userId) {
        Integer count = checkedInUsers.getOrDefault(userId, 0);
        checkedInUsers.put(userId, count + 1);
        Log.d("checkInUser", "Checking in user: " + userId + " with count: " + (count + 1));
        updateFirestore("checkedInUsers", checkedInUsers);
    }

    public HashMap<String, Integer> getCheckedInUsers() {
        return checkedInUsers;
    }

    public void setCheckedInUsers(HashMap<String, Integer> checkedInUsers) {
        this.checkedInUsers = checkedInUsers;
        updateFirestore("checkedInUsers", checkedInUsers);
    }



    /**
     * Converts the current event object into a map representation, suitable for Firestore storage.
     *
     * @return A map containing key-value pairs representing the event's properties.
     */
    Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("eventId", eventId);
        map.put("eventName", eventName);
        map.put("eventDate", eventDate);
        map.put("eventLocation", eventLocation);
        map.put("startTime", startTime);
        map.put("endTime", endTime);
        map.put("date", date);
        map.put("geolocation", geolocation);
        map.put("organizerOwnerId", organizerOwnerId);
        map.put("imagePosterId", imagePosterId);
        map.put("description", description);
        map.put("signupLimit", signupLimit);
        map.put("attendeesCount", attendeesCount);
        map.put("qrCode", qrCode);
        map.put("attendeeList", attendeeList);
        map.put("organizerMessages", organizerMessages);
        map.put("checkedInUsers", checkedInUsers);

        return map;
    }

    /**
     * Asynchronously retrieves an event from Firestore and performs a callback with the event object.
     *
     * @param eventId The ID of the event to retrieve.
     * @param callback The callback to execute once the event is retrieved or in case of an error.
     */
    public static void loadFromFirestore(String eventId, FirestoreCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(COLLECTION_PATH).document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Event event = documentSnapshot.toObject(Event.class);
                        callback.onCallback(event);
                    } else {
                        callback.onCallback(null);
                    }
                })
                .addOnFailureListener(e -> callback.onCallback(null));
    }

    /**
     * Callback interface for responding to asynchronous Firestore operations involving an Event object.
     */
    public interface FirestoreCallback {
        /**
         * Called when the Firestore operation is complete.
         *
         * @param event The event object returned by the Firestore operation, or null if an error occurred or the event doesn't exist.
         */
        void onCallback(Event event);
    }
}