package com.jazzee.jazzeejobzz;

public class Post {
    private String description;
    private String image;
    private String uid;
    private long timestamp;

    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Post(String description, String image, String uid, long timestamp) {
        this.description = description;
        this.image = image;
        this.uid = uid;
        this.timestamp = timestamp;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public String getUid() {
        return uid;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
