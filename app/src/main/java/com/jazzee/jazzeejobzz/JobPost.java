package com.jazzee.jazzeejobzz;

// JobPost.java
public class JobPost {
    private String id;
    private String jobRole;  // To match "jobRole" in the database
    private String jobDescription;  // To match "jobDescription" in the database
    private String status;
    private int applicantsCount;
    private String hirerName;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getJobRole() { return jobRole; }
    public void setJobRole(String jobRole) { this.jobRole = jobRole; }

    public String getJobDescription() { return jobDescription; }
    public void setJobDescription(String jobDescription) { this.jobDescription = jobDescription; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getApplicantsCount() { return applicantsCount; }
    public void setApplicantsCount(int applicantsCount) { this.applicantsCount = applicantsCount; }

    public String getHirerName() {
        return hirerName;
    }
    public void setHirerName(String hirerName) { this.hirerName = hirerName; }
}
