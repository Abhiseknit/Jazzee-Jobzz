package com.jazzee.jazzeejobzz;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.JobViewHolder> {

    private Context context;
    private List<Job> jobList;

    public void updateList(List<Job> filteredList) {
        this.jobList = filteredList;
        notifyDataSetChanged();
    }


    public JobAdapter(Context context, List<Job> jobList) {
        this.context = context;
        this.jobList = jobList;
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.job_card, parent, false);
        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {
        Job job = jobList.get(position);

        holder.jobTitle.setText(job.getJobRole());
        holder.companyLocation.setText(job.getCompanyLocation());
        holder.workType.setText(job.getWorkType());

        // Retrieve hirer details
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(job.getPostedBy());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String hirerNameStr = dataSnapshot.child("fullname").getValue(String.class);
                    String profileIconUrl = dataSnapshot.child("profileicon").getValue(String.class);

                    if (hirerNameStr != null) {
                        holder.hirerName.setText(hirerNameStr);
                    } else {
                        holder.hirerName.setText("N/A");
                    }

                    if (profileIconUrl != null) {
                        Glide.with(context)
                                .load(profileIconUrl)
                                .placeholder(R.drawable.profile) // Ensure you have a default placeholder
                                .into(holder.hirerProfileIcon);
                    } else {
                        holder.hirerProfileIcon.setImageResource(R.drawable.profile); // Set default image if URL is null
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });

        // Handle item click to open JobDetailActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, JobDetailActivity.class);
            intent.putExtra("jobID", job.getJobID());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public static class JobViewHolder extends RecyclerView.ViewHolder {

        TextView jobTitle, companyLocation, workType, hirerName;
        ImageView hirerProfileIcon;

        public JobViewHolder(@NonNull View itemView) {
            super(itemView);

            jobTitle = itemView.findViewById(R.id.job_title);
            companyLocation = itemView.findViewById(R.id.company_location);
            workType = itemView.findViewById(R.id.work_type);
            hirerName = itemView.findViewById(R.id.hirer_name);
            hirerProfileIcon = itemView.findViewById(R.id.hirer_profile_icon);
        }
    }
}
