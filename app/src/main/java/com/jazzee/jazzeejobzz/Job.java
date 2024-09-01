package com.jazzee.jazzeejobzz;

public class Job {
    private String jobID;
    private String jobRole;
    private String companyLocation;
    private String workType;
    private String jobType;
    private String salary;
    private String status;
    private String requiredSkills;
    private String jobDescription;
    private String postedBy;
    private String hirerName;
    public Job() {}

    public Job(String jobID, String jobRole, String companyLocation, String workType,
               String jobType,String status, String salary, String requiredSkills, String jobDescription, String postedBy, String hirerName) {
        this.jobID = jobID;
        this.status = status;
        this.jobRole = jobRole;
        this.companyLocation = companyLocation;
        this.workType = workType;
        this.jobType = jobType;
        this.salary = salary;
        this.requiredSkills = requiredSkills;
        this.jobDescription = jobDescription;
        this.postedBy = postedBy;
        this.hirerName = hirerName;
    }

    public String getJobID() { return jobID; }
    public void setJobID(String jobID) { this.jobID = jobID; }

    public String getJobRole() { return jobRole; }
    public void setJobRole(String jobRole) { this.jobRole = jobRole; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCompanyLocation() { return companyLocation; }
    public void setCompanyLocation(String companyLocation) { this.companyLocation = companyLocation; }

    public String getWorkType() { return workType; }
    public void setWorkType(String workType) { this.workType = workType; }

    public String getJobType() { return jobType; }
    public void setJobType(String jobType) { this.jobType = jobType; }

    public String getSalary() { return salary; }
    public void setSalary(String salary) { this.salary = salary; }

    public String getRequiredSkills() { return requiredSkills; }
    public void setRequiredSkills(String requiredSkills) { this.requiredSkills = requiredSkills; }

    public String getJobDescription() { return jobDescription; }
    public void setJobDescription(String jobDescription) { this.jobDescription = jobDescription; }

    public String getPostedBy() { return postedBy; }
    public void setPostedBy(String postedBy) { this.postedBy = postedBy; }

    public String getHirerName() {
        return hirerName;
    }

    public void setHirerName(String hirerName) {
        this.hirerName = hirerName;
    }
}
