package com.example.droiddesign.model;

/**
 * Represents a message sent by an organizer within the context of an event.
 * This class captures the content of the message, its posting date, and any associated image.
 */
public class OrganizerMessage {
	/**
	 * The date when the message was posted.
	 */
	private String date;

	/**
	 * The text content of the message.
	 */
	private String text;

	/**
	 * The identifier for any image uploaded alongside the message.
	 */
	private String imageUploadedPictureID;

	/**
	 * Constructs an OrganizerMessage with the specified date, text, and image identifier.
	 *
	 * @param date The date when the message was posted.
	 * @param text The textual content of the message.
	 * @param imageUploadedPictureID The identifier for the uploaded image, if any.
	 */
	public OrganizerMessage(String date, String text, String imageUploadedPictureID) {
		this.date = date;
		this.text = text;
		this.imageUploadedPictureID = imageUploadedPictureID;
	}
	// No-argument constructor required for Firebase deserialization
	public OrganizerMessage() {}

	/**
	 * Retrieves the date when the message was posted.
	 *
	 * @return The posting date of the message.
	 */
	public String getDate() {
		return date;
	}

	/**
	 * Sets the date when the message was posted.
	 *
	 * @param date The new posting date for the message.
	 */
	public void setDate(String date) {
		this.date = date;
	}

	/**
	 * Retrieves the text content of the message.
	 *
	 * @return The text of the message.
	 */
	public String getText() {
		return text;
	}

	/**
	 * Sets the text content of the message.
	 *
	 * @param text The new text content for the message.
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * Retrieves the identifier for any image uploaded with the message.
	 *
	 * @return The image identifier.
	 */
	public String getImageUploadedPictureID() {
		return imageUploadedPictureID;
	}

	/**
	 * Sets the identifier for an image uploaded with the message.
	 *
	 * @param imageUploadedPictureID The new identifier for the image.
	 */
	public void setImageUploadedPictureID(String imageUploadedPictureID) {
		this.imageUploadedPictureID = imageUploadedPictureID;
	}
}
