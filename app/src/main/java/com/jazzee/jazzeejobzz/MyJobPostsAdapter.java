package com.jazzee.jazzeejobzz;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
// Import additional required classes
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

// MyJobPostsAdapter.java
public class MyJobPostsAdapter extends RecyclerView.Adapter<MyJobPostsAdapter.ViewHolder> {

    private List<JobPost> jobList;
    private DatabaseReference jobsRef;
    private Context context;

    public MyJobPostsAdapter(Context context, List<JobPost> jobList) {
        this.context = context;
        this.jobList = jobList;
        this.jobsRef = FirebaseDatabase.getInstance().getReference().child("Jobs");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_job_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyJobPostsAdapter.ViewHolder holder, int position) {
        JobPost job = jobList.get(position);
        holder.jobRoleTextView.setText(job.getJobRole()); // Assuming job role is stored in job title
        holder.jobDescriptionTextView.setText(job.getJobDescription());


        // Handle Stop Hiring button
        holder.stopHiringButton.setOnClickListener(v -> {
            stopHiring(job.getId(), position);
        });

        // Handle View Applicants button
        holder.viewApplicantsButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, ViewApplicantsActivity.class);
            intent.putExtra("jobID", job.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    private void stopHiring(String jobId, int position) {
        jobsRef.child(jobId).child("status").setValue("inactive")
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Job posting stopped", Toast.LENGTH_SHORT).show();
                    jobList.remove(position);
                    notifyItemRemoved(position);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error stopping job posting", Toast.LENGTH_SHORT).show();
                });
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView jobRoleTextView;
        TextView jobDescriptionTextView;

        Button stopHiringButton;
        Button viewApplicantsButton;

        ViewHolder(View itemView) {
            super(itemView);
            jobRoleTextView = itemView.findViewById(R.id.textViewJobRole);
            jobDescriptionTextView = itemView.findViewById(R.id.textViewJobDescription);

            stopHiringButton = itemView.findViewById(R.id.buttonStopHiring);
            viewApplicantsButton = itemView.findViewById(R.id.buttonViewApplicants);
        }
    }
}
