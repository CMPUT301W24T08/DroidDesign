package com.example.droiddesign.model;

public class EventDetails {
	private String name;
	private String description;
	private String startTime;
	private String endTime;
	private String signupLimit;

	public EventDetails(String name, String description, String startTime, String endTime, String signupLimit, int signedUpAttendeesCount) {
		this.name = name;
		this.description = description;
		this.startTime = startTime;
		this.endTime = endTime;
		this.signupLimit = signupLimit;
		this.signedUpAttendeesCount = signedUpAttendeesCount;
	}

	private int signedUpAttendeesCount;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public String getSignupLimit() {
		return signupLimit;
	}

	public void setSignupLimit(String signupLimit) {
		this.signupLimit = signupLimit;
	}

	public int getSignedUpAttendeesCount() {
		return signedUpAttendeesCount;
	}

	public void setSignedUpAttendeesCount(int signedUpAttendeesCount) {
		this.signedUpAttendeesCount = signedUpAttendeesCount;
	}
}
