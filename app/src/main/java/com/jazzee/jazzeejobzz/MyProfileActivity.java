package com.jazzee.jazzeejobzz;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyProfileActivity extends AppCompatActivity {

    private static final String TAG = "MyProfileActivity";

    private ImageView profileIcon;
    private TextView profileFullName, profileUserName, profileCountry;
    private TextView profileCategory, about, otherDetails;
    private Button messageButton;
    private RecyclerView userPostsList;

    private FirebaseAuth auth;
    private DatabaseReference userRef, postsRef;
    private String currentUserId;
    private ArrayList<Post> postList;
    private PostAdapter postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        postsRef = FirebaseDatabase.getInstance().getReference().child("posts").child(currentUserId);

        profileIcon = findViewById(R.id.profile_icon);
        profileFullName = findViewById(R.id.profile_full_name);
        profileUserName = findViewById(R.id.profile_user_name);
        profileCountry = findViewById(R.id.profile_country);
        profileCategory = findViewById(R.id.profile_category);
        about = findViewById(R.id.about);
        otherDetails = findViewById(R.id.other_details);
        messageButton = findViewById(R.id.message_button);


        userPostsList = findViewById(R.id.user_posts_list);
        userPostsList.setLayoutManager(new LinearLayoutManager(this));
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(postList, this);
        userPostsList.setAdapter(postAdapter);



      // postList = findViewById(R.id.all_users_posts);
       // postList.setLayoutManager(new LinearLayoutManager(this));
        // posts = new ArrayList<>();
       // postAdapter = new PostAdapter(posts, this);
       // postList.setAdapter(postAdapter);

        loadProfileData();
        loadUserPosts();
    }

    private void loadProfileData() {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.d(TAG, "Profile data exists.");

                    if (dataSnapshot.hasChild("profileicon")) {
                        String profileImage = dataSnapshot.child("profileicon").getValue(String.class);
                        Log.d(TAG, "Profile image URL: " + profileImage);
                        Picasso.get().load(profileImage).placeholder(R.drawable.profile).into(profileIcon);
                    } else {
                        Log.d(TAG, "No profile image URL found.");
                    }

                    if (dataSnapshot.hasChild("fullname")) {
                        String fullName = dataSnapshot.child("fullname").getValue(String.class);
                        Log.d(TAG, "Full Name: " + fullName);
                        profileFullName.setText(fullName);
                    }

                    if (dataSnapshot.hasChild("username")) {
                        String userName = dataSnapshot.child("username").getValue(String.class);
                        Log.d(TAG, "Username: " + userName);
                        profileUserName.setText(userName);
                    }

                    if (dataSnapshot.hasChild("country")) {
                        String country = dataSnapshot.child("country").getValue(String.class);
                        Log.d(TAG, "Country: " + country);
                        profileCountry.setText(country);
                    }

                    if (dataSnapshot.hasChild("profilecategory")) {
                        String category = dataSnapshot.child("profilecategory").getValue(String.class);
                        Log.d(TAG, "Profile Category: " + category);
                        profileCategory.setText(category);
                    }

                    if (dataSnapshot.hasChild("about")) {
                        String aboutText = dataSnapshot.child("about").getValue(String.class);
                        Log.d(TAG, "About: " + aboutText);
                        about.setText(aboutText);
                    }

                    if (dataSnapshot.hasChild("otherdetails")) {
                        String details = dataSnapshot.child("otherdetails").getValue(String.class);
                        Log.d(TAG, "Other Details: " + details);
                        otherDetails.setText(details);
                    }
                } else {
                    Log.d(TAG, "Profile data does not exist.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error loading profile data: " + databaseError.getMessage());
                Toast.makeText(MyProfileActivity.this, "Error loading profile data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUserPosts() {
        postsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "Loading user posts...");
                postList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    if (post != null) {
                        postList.add(post);
                        Log.d(TAG, "Post added: " + post.getDescription());
                    }
                }
                Collections.reverse(postList); // To show the latest post first
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error loading posts: " + databaseError.getMessage());
                Toast.makeText(MyProfileActivity.this, "Error loading posts: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
