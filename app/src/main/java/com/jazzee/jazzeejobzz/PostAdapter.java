package com.jazzee.jazzeejobzz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private ArrayList<Post> posts;
    private Context context;
    private DatabaseReference UsersRef;

    public PostAdapter(ArrayList<Post> posts, Context context) {
        this.posts = posts;
        this.context = context;
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.captionTextView.setText(post.getDescription());

        // Load post image
        try {
            Picasso.get()
                    .load(post.getImage())
                    .resize(375, 375) // Adjust dimensions as needed
                    .centerCrop()
                    .into(holder.postImageView);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Load user details
        UsersRef.child(post.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String profileIconUrl = dataSnapshot.child("profileicon").getValue(String.class);
                    String fullName = dataSnapshot.child("fullname").getValue(String.class);
                    String username = dataSnapshot.child("username").getValue(String.class);

                    // Load profile icon
                    try {
                        if (profileIconUrl != null) {
                            Picasso.get().load(profileIconUrl).placeholder(R.drawable.profile).into(holder.profileImageView);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // Set user details
                    holder.fullNameTextView.setText(fullName != null ? fullName : "Unknown");
                    holder.usernameTextView.setText(username != null ? username : "Unknown");
                } else {
                    // Handle missing user data
                    holder.fullNameTextView.setText("Unknown");
                    holder.usernameTextView.setText("Unknown");
                    holder.profileImageView.setImageResource(R.drawable.profile); // Default profile image
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle potential errors
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {

        public ImageView postImageView;
        public ImageView profileImageView;
        public TextView fullNameTextView;
        public TextView usernameTextView;
        public TextView captionTextView;

        public PostViewHolder(View itemView) {
            super(itemView);

            postImageView = itemView.findViewById(R.id.post_image);
            profileImageView = itemView.findViewById(R.id.profile_image);
            fullNameTextView = itemView.findViewById(R.id.full_name);
            usernameTextView = itemView.findViewById(R.id.username);
            captionTextView = itemView.findViewById(R.id.caption);
        }
    }
}
