package com.jazzee.jazzeejobzz;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyJobPostsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MyJobPostsAdapter adapter;
    private List<JobPost> jobList;
    private DatabaseReference dbRef;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_job_posts);

        recyclerView = findViewById(R.id.recyclerViewMyJobPosts);
        jobList = new ArrayList<>();
        adapter = new MyJobPostsAdapter(this, jobList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        dbRef = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();

        loadJobPosts();
    }

    private void loadJobPosts() {
        String userId = auth.getCurrentUser().getUid();
        dbRef.child("Jobs").orderByChild("postedBy").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        jobList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            JobPost job = snapshot.getValue(JobPost.class);
                            if (job != null) {
                                // Exclude inactive jobs
                                if (!"inactive".equals(job.getStatus())) {
                                    job.setId(snapshot.getKey());
                                    loadApplicantsCount(job);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(MyJobPostsActivity.this, "Error loading job posts", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadApplicantsCount(JobPost job) {
        dbRef.child("job_applications").orderByChild("jobId").equalTo(job.getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int applicantsCount = (int) dataSnapshot.getChildrenCount();
                        job.setApplicantsCount(applicantsCount);
                        jobList.add(job);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(MyJobPostsActivity.this, "Error loading applicants count", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
