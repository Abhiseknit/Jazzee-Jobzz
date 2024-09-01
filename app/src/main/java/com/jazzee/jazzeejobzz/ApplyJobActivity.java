package com.jazzee.jazzeejobzz;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ApplyJobActivity extends AppCompatActivity {

    private static final int PICK_RESUME_REQUEST = 1;
    private EditText applicantName, applicantPhone, applicantAge, applicantEducation, applicantExperience, applicantReason, applicantCurrentJobStudy;
    private Button submitApplicationButton, uploadResumeButton;
    private Uri resumeUri;
    private String jobID, resumeUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_job);

        // Initialize views
        applicantName = findViewById(R.id.applicant_name);
        applicantPhone = findViewById(R.id.applicant_phone);
        applicantAge = findViewById(R.id.applicant_age);
        applicantEducation = findViewById(R.id.applicant_education);
        applicantExperience = findViewById(R.id.applicant_experience);
        applicantReason = findViewById(R.id.applicant_interest);
        applicantCurrentJobStudy = findViewById(R.id.applicant_current_job_study);
        submitApplicationButton = findViewById(R.id.submit_application_button);
        uploadResumeButton = findViewById(R.id.upload_resume_button);

        // Get jobID from intent
        jobID = getIntent().getStringExtra("jobID");


        uploadResumeButton.setOnClickListener(v -> openFileChooser());

        submitApplicationButton.setOnClickListener(v -> submitApplication());
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(intent, PICK_RESUME_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_RESUME_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            resumeUri = data.getData();
            uploadResume();
        }
    }

    private void uploadResume() {
        if (resumeUri != null) {
            String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                    .child("resumes").child(userID + ".pdf");

            storageRef.putFile(resumeUri).addOnSuccessListener(taskSnapshot ->
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        resumeUrl = uri.toString();
                        Toast.makeText(ApplyJobActivity.this, "Resume uploaded successfully", Toast.LENGTH_SHORT).show();
                    })).addOnFailureListener(e ->
                    Toast.makeText(ApplyJobActivity.this, "Failed to upload resume", Toast.LENGTH_SHORT).show()
            );
        }
    }

    private void submitApplication() {
        String name = applicantName.getText().toString().trim();
        String phone = applicantPhone.getText().toString().trim();
        String age = applicantAge.getText().toString().trim();
        String education = applicantEducation.getText().toString().trim();
        String experience = applicantExperience.getText().toString().trim();
        String reason = applicantReason.getText().toString().trim();
        String currentJobStudy = applicantCurrentJobStudy.getText().toString().trim();

        // Check if all fields are filled
        if (name.isEmpty() || phone.isEmpty() || age.isEmpty() || education.isEmpty() || experience.isEmpty() || reason.isEmpty() || currentJobStudy.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if resume has been uploaded
        if (resumeUrl == null || resumeUrl.isEmpty()) {
            Toast.makeText(this, "Please upload your resume before submitting the application", Toast.LENGTH_SHORT).show();
            return;
        }

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String applicationID = FirebaseDatabase.getInstance().getReference().push().getKey();

        // Create the JobApplication object
        JobApplication application = new JobApplication(applicationID, jobID, name, phone, age, education, reason, experience, currentJobStudy, resumeUrl);

        // Reference to save the application under the specific jobID
        DatabaseReference applicationsRef = FirebaseDatabase.getInstance().getReference()
                .child("job_applications").child(jobID).child(applicationID);

        applicationsRef.setValue(application).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(ApplyJobActivity.this, "Application submitted successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(ApplyJobActivity.this, "Failed to submit application", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
