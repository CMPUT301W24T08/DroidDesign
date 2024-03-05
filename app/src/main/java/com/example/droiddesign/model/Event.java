package com.example.droiddesign.model;


import com.google.firebase.firestore.FirebaseFirestore;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Event {
    // Dummy class that we can either forget about or flesh out more.
    // ArrayList<Organizer> organizers;

        private String eventId;
        private String hashedId;
        private String eventName;
        private String eventDate;
        private String startTime;
        private String endTime;
        private String date;
        private String geolocation;
        private String organizerOwnerId;
        private String imagePosterId;
        private String description;
        private Integer signupLimit;
        private Integer attendeesCount;
        private String qrCode;
        private List<String> attendeeList;
        private List<OrganizerMessage> organizerMessages;
        private static final String COLLECTION_PATH = "events";

        public Event(String eventId, String eventName, String eventDate, String startTime, String endTime, String geolocation, String organizerOwnerId, String imagePosterId, String description, Integer signupLimit, Integer attendeesCount, String qrCode) throws NoSuchAlgorithmException {
            this.eventName = eventName;
            this.eventDate = eventDate;
            this.startTime = startTime;
            this.endTime = endTime;
            this.geolocation = geolocation;
            this.organizerOwnerId = organizerOwnerId;
            this.imagePosterId = imagePosterId;
            this.description = description;
            this.signupLimit = signupLimit;
            this.attendeesCount = attendeesCount;
            this.qrCode = qrCode;
            this.hashedId = generateHashId();
        }
        // Getters and Setters
        public String getEventId() {
            return eventId;
        }

        public void setEventId(String eventId) {
            this.eventId = eventId;
        }

        public String getEventName() {
            return eventName;
        }

        public void setEventName(String eventName) {
            this.eventName = eventName;
        }

        public String getEventDate() {
            return eventDate;
        }

        public void setEventDate(String eventDate) {
            this.eventDate = eventDate;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getGeolocation() {
            return geolocation;
        }

        public void setGeolocation(String geolocation) {
            this.geolocation = geolocation;
        }

        public String getOrganizerOwnerId() {
            return organizerOwnerId;
        }

        public void setOrganizerOwnerId(String organizerOwnerId) {
            this.organizerOwnerId = organizerOwnerId;
        }

        public String getImagePosterId() {
            return imagePosterId;
        }

        public void setImagePosterId(String imagePosterId) {
            this.imagePosterId = imagePosterId;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Integer getSignupLimit() {
            return signupLimit;
        }

        public void setSignupLimit(Integer signupLimit) {
            this.signupLimit = signupLimit;
        }

        public Integer getAttendeesCount() {
            return attendeesCount;
        }

        public void setAttendeesCount(Integer attendeesCount) {
            this.attendeesCount = attendeesCount;
        }

        public String getQrCode() {
            return qrCode;
        }

        public void setQrCode(String qrCode) {
            this.qrCode = qrCode;
        }

        public List<String> getAttendeeList() {
            return attendeeList;
        }

        public void setAttendeeList(List<String> attendeeList) {
            this.attendeeList = attendeeList;
        }

        public List<OrganizerMessage> getOrganizerMessages() {
            return organizerMessages;
        }

        public void setOrganizerMessages(List<OrganizerMessage> organizerMessages) {
            this.organizerMessages = organizerMessages;
        }




    // Getters and Setters
    public String getHashId(){return hashedId;}


    // Helper method to convert the event object to a Map
    public HashMap<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("eventId", hashedId);
        map.put("eventName", eventName);
        map.put("eventDate", eventDate);
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
        return map;
    }

    public String generateHashId() throws NoSuchAlgorithmException {
        String combinedString = this.eventDate + this.qrCode + this.eventName;


        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(combinedString.getBytes());


        byte[] byteString = md.digest();


        StringBuilder sb = new StringBuilder();
        for(byte b: byteString){
            sb.append(String.format("%02x",b));
        }
        return sb.toString();


    }




}
