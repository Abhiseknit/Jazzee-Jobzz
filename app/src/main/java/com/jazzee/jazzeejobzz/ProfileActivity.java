package com.jazzee.jazzeejobzz;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private ImageView profileIcon;
    private TextView fullName, username, country, profileCategory, about, otherDetails;
    private RecyclerView recyclerViewPosts;
    private PostAdapter postAdapter;
    private List<Post> postList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize views
        profileIcon = findViewById(R.id.profileIcon);
        fullName = findViewById(R.id.fullName);
        username = findViewById(R.id.username);
        country = findViewById(R.id.country);
        profileCategory = findViewById(R.id.profileCategory);
        about = findViewById(R.id.about);
        otherDetails = findViewById(R.id.otherDetails);
        recyclerViewPosts = findViewById(R.id.recyclerViewPosts);

        // Initialize RecyclerView
        recyclerViewPosts.setLayoutManager(new LinearLayoutManager(this));
        postList = new ArrayList<>();

        // Get userId from intent
        String userId = getIntent().getStringExtra("userId");
        if (userId != null) {
            loadUserProfile(userId);
            loadUserPosts(userId);
        } else {
            Toast.makeText(this, "Failed to load user profile", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if userId is null
        }
    }

    private void loadUserProfile(String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        // Populate your UI with the user's data here
                        fullName.setText(user.getFullname());
                        username.setText(user.getUsername());
                        country.setText(user.getCountry());
                        profileCategory.setText(user.getProfilecategory());
                        about.setText(user.getAbout());
                        otherDetails.setText(user.getOtherdetails());

                        // Load profile icon using Picasso
                        if (user.getProfileicon() != null) {
                            Picasso.get().load(user.getProfileicon()).into(profileIcon);
                        }else {
                            profileIcon.setImageResource(R.drawable.profile); // Use a default icon if none is provided
                        }
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Error loading user profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUserPosts(String userId) {
        DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference().child("posts").child(userId);
        postsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    if (post != null) {
                        postList.add(post);
                    }
                }
                // Initialize and set the PostAdapter
                postAdapter = new PostAdapter(new ArrayList<>(postList), ProfileActivity.this);
                // Passing postList first, then context
                recyclerViewPosts.setAdapter(postAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Error loading user posts", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
