package com.jazzee.jazzeejobzz;

public class Applicant {
    private String applicationId;  // Unique ID for each application
    private String jobId;  // ID of the job this application is linked to
    private String fullName;
    private String username;
    private String userId;  // Added userId for profile icon retrieval
    private String phoneNumber;
    private String age;
    private String educationalDetails;
    private String experience;
    private String resumeLink;
    private String whyInterested;

    public Applicant() {
    }

    public Applicant(String applicationId, String jobId, String fullName, String username, String userId,
                     String phoneNumber, String age, String educationalDetails, String experience,
                     String resumeLink, String whyInterested) {
        this.applicationId = applicationId;
        this.jobId = jobId;
        this.fullName = fullName;
        this.username = username;
        this.userId = userId;  // Initialize userId
        this.phoneNumber = phoneNumber;
        this.age = age;
        this.educationalDetails = educationalDetails;
        this.experience = experience;
        this.resumeLink = resumeLink;
        this.whyInterested = whyInterested;
    }

    // Getters and setters for all fields
    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;  // Setter for userId
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getEducationalDetails() {
        return educationalDetails;
    }

    public void setEducationalDetails(String educationalDetails) {
        this.educationalDetails = educationalDetails;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getResumeLink() {
        return resumeLink;
    }

    public void setResumeLink(String resumeLink) {
        this.resumeLink = resumeLink;
    }

    public String getWhyInterested() {
        return whyInterested;
    }

    public void setWhyInterested(String whyInterested) {
        this.whyInterested = whyInterested;
    }
}
