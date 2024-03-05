package com.example.droiddesign.controller;

public class Upload {
    private String mName;
    private String mImageUrl;

    // Default constructor is required for calls to DataSnapshot.getValue(Upload.class)
    public Upload() {
    }

    public Upload(String name, String imageUrl) {
        // If the name is empty, set it to "No Name"
        if (name.trim().equals("")) {
            this.mName = "No Name";
        } else {
            this.mName = name;
        }
        this.mImageUrl = imageUrl;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.mImageUrl = imageUrl;
    }
}
