package com.jazzee.jazzeejobzz;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class PostJobActivity extends AppCompatActivity {

    private EditText jobRole, requiredSkills, companyLocation, salary, jobDescription;
    private RadioGroup jobTypeGroup, workTypeGroup;
    private Button saveJobBtn;
    private DatabaseReference JobsRef;
    private FirebaseAuth auth;
    private String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_job);

        // Initialize Firebase references
        auth = FirebaseAuth.getInstance();
        currentUserID = auth.getCurrentUser().getUid();
        JobsRef = FirebaseDatabase.getInstance().getReference().child("Jobs");

        // Link XML elements to Java code
        jobRole = findViewById(R.id.job_role);
        requiredSkills = findViewById(R.id.required_skills);
        companyLocation = findViewById(R.id.company_location);
        salary = findViewById(R.id.salary);
        jobDescription = findViewById(R.id.job_description);
        jobTypeGroup = findViewById(R.id.job_type_group);
        workTypeGroup = findViewById(R.id.work_type_group);
        saveJobBtn = findViewById(R.id.save_job_btn);

        saveJobBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveJobDetails();
            }
        });
    }

    private void saveJobDetails() {
        // Retrieve data from input fields
        String jobRoleStr = jobRole.getText().toString().trim();
        String requiredSkillsStr = requiredSkills.getText().toString().trim();
        String companyLocationStr = companyLocation.getText().toString().trim();
        String salaryStr = salary.getText().toString().trim();
        String jobDescriptionStr = jobDescription.getText().toString().trim();

        // Get selected job type and work type
        int selectedJobTypeId = jobTypeGroup.getCheckedRadioButtonId();
        int selectedWorkTypeId = workTypeGroup.getCheckedRadioButtonId();
        RadioButton selectedJobTypeBtn = findViewById(selectedJobTypeId);
        RadioButton selectedWorkTypeBtn = findViewById(selectedWorkTypeId);

        // Validate inputs
        if (jobRoleStr.isEmpty() || requiredSkillsStr.isEmpty() || companyLocationStr.isEmpty() ||
                salaryStr.isEmpty() || jobDescriptionStr.isEmpty() || selectedJobTypeBtn == null ||
                selectedWorkTypeBtn == null) {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a unique job ID
        String jobID = JobsRef.push().getKey();

        // Create a map to hold job data
        HashMap<String, Object> jobMap = new HashMap<>();
        jobMap.put("jobID", jobID);
        jobMap.put("jobRole", jobRoleStr);
        jobMap.put("requiredSkills", requiredSkillsStr);
        jobMap.put("companyLocation", companyLocationStr);
        jobMap.put("salary", salaryStr);
        jobMap.put("jobDescription", jobDescriptionStr);
        jobMap.put("jobType", selectedJobTypeBtn.getText().toString());
        jobMap.put("workType", selectedWorkTypeBtn.getText().toString());
        jobMap.put("postedBy", currentUserID);

        // Save job details to Firebase
        JobsRef.child(jobID).setValue(jobMap).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(PostJobActivity.this, "Job Posted Successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(PostJobActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
