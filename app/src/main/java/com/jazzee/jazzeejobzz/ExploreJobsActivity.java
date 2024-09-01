package com.jazzee.jazzeejobzz;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

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

public class ExploreJobsActivity extends AppCompatActivity {

    private EditText searchField;
    private RecyclerView exploreJobsRecyclerView;
    private JobAdapter jobAdapter;
    private List<Job> jobList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_jobs);

        searchField = findViewById(R.id.search_field);
        exploreJobsRecyclerView = findViewById(R.id.explore_jobs_recyclerview);
        exploreJobsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        jobList = new ArrayList<>();
        jobAdapter = new JobAdapter(this, jobList);
        exploreJobsRecyclerView.setAdapter(jobAdapter);

        DatabaseReference jobsRef = FirebaseDatabase.getInstance().getReference().child("Jobs");

        jobsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                jobList.clear();
                for (DataSnapshot jobSnapshot : snapshot.getChildren()) {
                    Job job = jobSnapshot.getValue(Job.class);

                    // Ensure the job is active before processing
                    if (job != null && !"inactive".equals(job.getStatus())) {
                        String hirerId = job.getPostedBy();
                        fetchHirerName(hirerId, job);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors.
            }
        });

        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterJobs(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Not needed
            }
        });
    }

    private void fetchHirerName(String hirerId, Job job) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(hirerId);
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String hirerName = snapshot.child("fullName").getValue(String.class);
                    job.setHirerName(hirerName);
                }
                jobList.add(job);
                jobAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors.
            }
        });
    }

    private void filterJobs(String query) {
        List<Job> filteredList = new ArrayList<>();
        for (Job job : jobList) {
            String jobTitle = job.getJobRole() != null ? job.getJobRole().toLowerCase() : "";
            String companyLocation = job.getCompanyLocation() != null ? job.getCompanyLocation().toLowerCase() : "";
            String requiredSkills = job.getRequiredSkills() != null ? job.getRequiredSkills().toLowerCase() : "";
            String hirerName = job.getHirerName() != null ? job.getHirerName().toLowerCase() : "";

            if (jobTitle.contains(query.toLowerCase()) ||
                    companyLocation.contains(query.toLowerCase()) ||
                    requiredSkills.contains(query.toLowerCase()) ||
                    hirerName.contains(query.toLowerCase())) {
                filteredList.add(job);
            }
        }
        jobAdapter.updateList(filteredList);
    }

}
