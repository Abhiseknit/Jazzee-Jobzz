package com.jazzee.jazzeejobzz;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationView;
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

public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private RecyclerView postList;
    private FirebaseAuth auth;
    private GoogleSignInClient googleSignInClient;
    private Toolbar maintoolbar;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private ImageView NavProfileIcon;
    private TextView NavProfileUserName;
    private DatabaseReference UsersRef;
    private DatabaseReference PostsRef;
    private ArrayList<Post> posts;
    private PostAdapter postAdapter;
    private String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        maintoolbar = findViewById(R.id.main_pagetoolbar);
        setSupportActionBar(maintoolbar);
        auth = FirebaseAuth.getInstance();
        currentUserID = auth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        PostsRef = FirebaseDatabase.getInstance().getReference().child("posts"); // Reference to posts

        // Google SignIn Client setup
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        drawerLayout = findViewById(R.id.drawable_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView = findViewById(R.id.navigation_view);
        View navView = navigationView.getHeaderView(0);
        NavProfileIcon = navView.findViewById(R.id.nav_profileicon);
        NavProfileUserName = navView.findViewById(R.id.nav_fullname);

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("fullname")) {
                        String fullname = dataSnapshot.child("fullname").getValue().toString();
                        NavProfileUserName.setText(fullname);
                    }
                    if (dataSnapshot.hasChild("profileicon")) {
                        String image = dataSnapshot.child("profileicon").getValue().toString();
                        Picasso.get().load(image).placeholder(R.drawable.profile).into(NavProfileIcon);
                    } else {
                        Toast.makeText(MainActivity.this, "Profile image does not exist...", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Error loading profile data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Setup RecyclerView for posts
        postList = findViewById(R.id.all_users_posts);
        postList.setLayoutManager(new LinearLayoutManager(this));
        posts = new ArrayList<>();
        postAdapter = new PostAdapter(posts, this);

        postList.setAdapter(postAdapter);

        // Load posts from Firebase
        loadPosts();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                UserMenuSelector(item);
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void UserMenuSelector(MenuItem item) {
        if (item.getItemId() == R.id.nav_profile) {
            Intent exploreMyProfile = new Intent(MainActivity.this, MyProfileActivity.class);
            startActivity(exploreMyProfile);
        } else if (item.getItemId() == R.id.nav_home) {
            Intent exploreMain = new Intent(MainActivity.this, MainActivity.class);
            startActivity(exploreMain);
        } else if (item.getItemId() == R.id.nav_settings) {
            Intent exploreSetting = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(exploreSetting);

        } else if (item.getItemId() == R.id.nav_findconnections) {
            Intent exploreSearch = new Intent(MainActivity.this, FindConnectionsActivity.class);
            startActivity(exploreSearch);
        } else if (item.getItemId() == R.id.nav_logout) {
            auth.signOut();
            googleSignInClient.signOut().addOnCompleteListener(task -> {
                Intent intent = new Intent(MainActivity.this, SplashActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();  // Ensure the MainActivity is closed
            });

        } else if (item.getItemId() == R.id.nav_postjob) {
            Intent postJobIntent = new Intent(MainActivity.this, PostJobActivity.class);
            startActivity(postJobIntent);
        } else if (item.getItemId() == R.id.nav_makepost) {
            Intent postIntent = new Intent(MainActivity.this, PostActivity.class);
            startActivity(postIntent);
        } else if (item.getItemId() == R.id.nav_findjob) {
            Intent exploreJobsIntent = new Intent(MainActivity.this, ExploreJobsActivity.class);
            startActivity(exploreJobsIntent);
        } else if (item.getItemId() == R.id.nav_myjobposts) {
            Intent myJobPostsIntent = new Intent(MainActivity.this, MyJobPostsActivity.class);
            startActivity(myJobPostsIntent);
        } else {
            throw new IllegalStateException("Unexpected value: " + item.getItemId());
        }
    }

    private void loadPosts() {
        PostsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                posts.clear();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot postSnapshot : userSnapshot.getChildren()) {
                        Post post = postSnapshot.getValue(Post.class);
                        if (post != null) {
                            posts.add(post);
                        }
                    }
                }
                Collections.reverse(posts);
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Error loading posts: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
