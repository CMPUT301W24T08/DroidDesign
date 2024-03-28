package com.example.droiddesign.controller;

public class UploadQR {
    String qrUrl, eventId, type;

    public UploadQR(String qrUrl, String eventId, String type) {
        this.qrUrl = qrUrl;
        this.eventId = eventId;
        this.type = type;
    }

    public String getQrUrl() {
        return qrUrl;
    }

    public void setQrUrl(String qrUrl) {
        this.qrUrl = qrUrl;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}