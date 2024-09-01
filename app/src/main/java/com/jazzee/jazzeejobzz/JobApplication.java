package com.jazzee.jazzeejobzz;

public class JobApplication {
    private String applicationId;
    private String jobId;  // Field for linking the application to a specific job
    private String applicantName;
    private String applicantPhone;
    private String applicantAge;
    private String applicantEducation;
    private String applicantInterest;
    private String applicantExperience;
    private String applicantCurrentJobStudy;
    private String resumeUrl;  // Field for storing resume URL

    public JobApplication() {
    }

    public JobApplication(String applicationId, String jobId, String applicantName, String applicantPhone,
                          String applicantAge, String applicantEducation, String applicantInterest,
                          String applicantExperience, String applicantCurrentJobStudy, String resumeUrl) {
        this.applicationId = applicationId;
        this.jobId = jobId;
        this.applicantName = applicantName;
        this.applicantPhone = applicantPhone;
        this.applicantAge = applicantAge;
        this.applicantEducation = applicantEducation;
        this.applicantInterest = applicantInterest;
        this.applicantExperience = applicantExperience;
        this.applicantCurrentJobStudy = applicantCurrentJobStudy;
        this.resumeUrl = resumeUrl;
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

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public String getApplicantPhone() {
        return applicantPhone;
    }

    public void setApplicantPhone(String applicantPhone) {
        this.applicantPhone = applicantPhone;
    }

    public String getApplicantAge() {
        return applicantAge;
    }

    public void setApplicantAge(String applicantAge) {
        this.applicantAge = applicantAge;
    }

    public String getApplicantEducation() {
        return applicantEducation;
    }

    public void setApplicantEducation(String applicantEducation) {
        this.applicantEducation = applicantEducation;
    }

    public String getApplicantInterest() {
        return applicantInterest;
    }

    public void setApplicantInterest(String applicantInterest) {
        this.applicantInterest = applicantInterest;
    }

    public String getApplicantExperience() {
        return applicantExperience;
    }

    public void setApplicantExperience(String applicantExperience) {
        this.applicantExperience = applicantExperience;
    }

    public String getApplicantCurrentJobStudy() {
        return applicantCurrentJobStudy;
    }

    public void setApplicantCurrentJobStudy(String applicantCurrentJobStudy) {
        this.applicantCurrentJobStudy = applicantCurrentJobStudy;
    }

    public String getResumeUrl() {
        return resumeUrl;
    }

    public void setResumeUrl(String resumeUrl) {
        this.resumeUrl = resumeUrl;
    }
}
