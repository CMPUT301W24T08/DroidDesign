package com.example.droiddesign.controller;

public class UploadQR {
    String qrUrl, qrId, eventId, eventName, type;

    public UploadQR(String qrUrl, String qrId, String eventId, String eventName, String type) {
        this.qrUrl = qrUrl;
        this.qrId = qrId;
        this.eventId = eventId;
        this.eventName = eventName;
        this.type = type;
    }

    public String getQrUrl() {
        return qrUrl;
    }

    public void setQrUrl(String qrUrl) {
        this.qrUrl = qrUrl;
    }

    public String getQrId() {
        return qrId;
    }

    public void setQrId(String qrId) {
        this.qrId = qrId;
    }

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
