package com.jazzee.jazzeejobzz;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class ApplicantAdapter extends RecyclerView.Adapter<ApplicantAdapter.ApplicantViewHolder> {

    private static final String TAG = "ApplicantAdapter";
    private Context context;
    private List<Applicant> applicantList;

    public ApplicantAdapter(Context context, List<Applicant> applicantList) {
        this.context = context;
        this.applicantList = applicantList;
    }

    @NonNull
    @Override
    public ApplicantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_applicant, parent, false);
        return new ApplicantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApplicantViewHolder holder, int position) {
        Applicant applicant = applicantList.get(position);

        // Log applicant details for debugging
        Log.d(TAG, "Binding applicant: " + applicant.getFullName() + ", " + applicant.getUserId());

        // Set applicant details
        holder.fullName.setText(applicant.getFullName());
        holder.username.setText(applicant.getUsername());
        holder.phoneNumber.setText(applicant.getPhoneNumber());
        holder.age.setText(applicant.getAge());
        holder.education.setText(applicant.getEducationalDetails());
        holder.experience.setText(applicant.getExperience());
        holder.interestReason.setText(applicant.getWhyInterested());

        // Handle resume link click
        String resumeUrl = applicant.getResumeLink();
        if (resumeUrl != null && !resumeUrl.isEmpty()) {
            holder.resumeLink.setOnClickListener(v -> {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(resumeUrl));
                context.startActivity(browserIntent);
            });
        } else {
            holder.resumeLink.setText("Resume link not available");
        }

        // Load profile icon using Glide
        String userId = applicant.getUserId();
        if (userId != null && !userId.isEmpty()) {
            StorageReference profileIconRef = FirebaseStorage.getInstance().getReference()
                    .child("ProfileIcons/" + userId + ".jpg");
            Glide.with(context)
                    .load(profileIconRef)
                    .placeholder(R.drawable.profile)
                    .error(R.drawable.profile)  // Optional: Use error placeholder for debugging
                    .into(holder.profileIcon);
        } else {
            holder.profileIcon.setImageResource(R.drawable.profile);  // Use default placeholder
        }
    }

    @Override
    public int getItemCount() {
        return applicantList.size();
    }

    public static class ApplicantViewHolder extends RecyclerView.ViewHolder {

        public TextView fullName, username, phoneNumber, age, education, experience, resumeLink, interestReason;
        public ImageView profileIcon;

        public ApplicantViewHolder(@NonNull View itemView) {
            super(itemView);

            fullName = itemView.findViewById(R.id.applicant_full_name);
            username = itemView.findViewById(R.id.applicant_username);
            phoneNumber = itemView.findViewById(R.id.applicant_phone_number);
            age = itemView.findViewById(R.id.applicant_age);
            education = itemView.findViewById(R.id.applicant_education);
            experience = itemView.findViewById(R.id.applicant_experience);
            resumeLink = itemView.findViewById(R.id.applicant_resume_link);
            interestReason = itemView.findViewById(R.id.applicant_interest_reason);
            profileIcon = itemView.findViewById(R.id.applicant_profile_icon);
        }
    }
}
