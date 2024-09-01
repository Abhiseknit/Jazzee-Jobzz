package com.jazzee.jazzeejobzz;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewApplicantsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Applicant> applicantList;  // Change to List<Applicant>
    private ApplicantAdapter applicantAdapter;

    private String jobID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_applicants);

        recyclerView = findViewById(R.id.recycler_view_applicants);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        applicantList = new ArrayList<>();
        applicantAdapter = new ApplicantAdapter(this, applicantList);  // Pass context and list
        recyclerView.setAdapter(applicantAdapter);

        // Get jobID from intent
        jobID = getIntent().getStringExtra("jobID");
        if (jobID == null) {
            Toast.makeText(this, "Error: jobID is null", Toast.LENGTH_SHORT).show();
            return; // Exit if jobID is null
        }
        // Load applicants from Firebase
        loadApplicants();
    }

    private void loadApplicants() {
        DatabaseReference applicantsRef = FirebaseDatabase.getInstance().getReference()
                .child("job_applications").child(jobID);

        applicantsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                applicantList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    JobApplication jobApplication = snapshot.getValue(JobApplication.class);
                    if (jobApplication != null) {
                        // Convert JobApplication to Applicant
                        Applicant applicant = new Applicant(
                                jobApplication.getApplicationId(),
                                jobApplication.getJobId(),
                                jobApplication.getApplicantName(),
                                "",  // Assuming username is not in JobApplication
                                jobApplication.getJobId(),  // Assuming userId is same as jobId for demonstration
                                jobApplication.getApplicantPhone(),
                                jobApplication.getApplicantAge(),
                                jobApplication.getApplicantEducation(),
                                jobApplication.getApplicantExperience(),
                                jobApplication.getResumeUrl(),
                                jobApplication.getApplicantInterest()
                        );
                        applicantList.add(applicant);
                    }
                }
                applicantAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }
}
