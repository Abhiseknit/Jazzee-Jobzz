package com.jazzee.jazzeejobzz;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class JobDetailActivity extends AppCompatActivity {

    private TextView jobTitle, companyLocation, salary, requiredSkills, jobDescription, jobType, workType, hirerName;
    private ImageView hirerProfileIcon;
    private Button applyNowButton, viewApplicantsButton; // Added button to view applicants

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_detail);

        // Initialize Views
        jobTitle = findViewById(R.id.job_title);
        companyLocation = findViewById(R.id.company_location);
        salary = findViewById(R.id.salary);
        requiredSkills = findViewById(R.id.required_skills);
        jobDescription = findViewById(R.id.job_description);
        jobType = findViewById(R.id.job_type);
        workType = findViewById(R.id.work_type);
        hirerName = findViewById(R.id.hirer_name);
        hirerProfileIcon = findViewById(R.id.hirer_profile_icon);
        applyNowButton = findViewById(R.id.apply_now_button);


        // Get jobID from intent
        String jobID = getIntent().getStringExtra("jobID");

        // Set up Apply Now button to open ApplyJobActivity
        applyNowButton.setOnClickListener(v -> {
            Intent intent = new Intent(JobDetailActivity.this, ApplyJobActivity.class);
            intent.putExtra("jobID", jobID);
            startActivity(intent);
        });

        // Set up View Applicants button to open ViewApplicantsActivity


        // Retrieve job details
        DatabaseReference jobRef = FirebaseDatabase.getInstance().getReference().child("Jobs").child(jobID);
        jobRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Job job = dataSnapshot.getValue(Job.class);
                    if (job != null) {
                        jobTitle.setText(job.getJobRole());
                        companyLocation.setText(job.getCompanyLocation());
                        salary.setText(job.getSalary());
                        requiredSkills.setText(job.getRequiredSkills());
                        jobDescription.setText(job.getJobDescription());
                        jobType.setText(job.getJobType());
                        workType.setText(job.getWorkType());

                        // Retrieve hirer details
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(job.getPostedBy());
                        userRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    String hirerNameStr = dataSnapshot.child("fullname").getValue(String.class);
                                    String profileIconUrl = dataSnapshot.child("profileicon").getValue(String.class);

                                    if (hirerNameStr != null) {
                                        hirerName.setText(hirerNameStr);
                                    } else {
                                        hirerName.setText("N/A");
                                    }

                                    if (profileIconUrl != null) {
                                        Glide.with(JobDetailActivity.this)
                                                .load(profileIconUrl)
                                                .placeholder(R.drawable.profile) // Ensure you have a default placeholder
                                                .into(hirerProfileIcon);
                                    } else {
                                        hirerProfileIcon.setImageResource(R.drawable.profile); // Set default image if URL is null
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Handle possible errors
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }
}
