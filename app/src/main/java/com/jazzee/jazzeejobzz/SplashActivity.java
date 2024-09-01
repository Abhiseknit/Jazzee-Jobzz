package com.jazzee.jazzeejobzz;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jazzee.jazzeejobzz.databinding.ActivitySplashBinding;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;
    private GoogleSignInOptions signInOptions;
    private GoogleSignInClient signInClient;
    private FirebaseAuth auth;
    private DatabaseReference UsersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupSignIn();
    }

    // Set up Google Sign-In options
    private void setupSignIn() {
        auth = FirebaseAuth.getInstance();
        signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        signInClient = GoogleSignIn.getClient(this, signInOptions);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            Log.d("SplashActivity", "User is already signed in.");
            checkUserProfileCompletion(currentUser.getUid());
        } else {
            Log.d("SplashActivity", "No user signed in, starting Google Sign-In.");
            signIn();
        }
    }

    // Check if the user profile is complete
    private void checkUserProfileCompletion(String userId) {
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        UsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() &&
                        dataSnapshot.hasChild("username") &&
                        dataSnapshot.hasChild("fullname") &&
                        dataSnapshot.hasChild("country") &&
                        dataSnapshot.hasChild("profileicon")) {

                    Log.d("SplashActivity", "User profile is complete.");
                    loadProfileImageAndProceed(userId);
                } else {
                    Log.d("SplashActivity", "User profile is incomplete, navigating to SetupActivity.");
                    navigateToSetupActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("SplashActivity", "Failed to check user data: " + databaseError.getMessage());
                Toast.makeText(SplashActivity.this, "Failed to check user data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Load profile image using Glide and then navigate to MainActivity
    private void loadProfileImageAndProceed(String userId) {
        StorageReference profileImageRef = FirebaseStorage.getInstance().getReference().child("profile_images/" + userId + ".jpg");

        profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            // Load the image into ImageView if needed (optional)
            Glide.with(SplashActivity.this)
                    .load(uri)
                    .placeholder(R.drawable.profile) // Placeholder image
                    .error(R.drawable.ic_backicon)   // Error image
                    .into(binding.imageView);        // Assuming you have an ImageView with this ID

            navigateToMainActivity();
        }).addOnFailureListener(exception -> {
            Log.e("SplashActivity", "Failed to load profile image: " + exception.getMessage());
            // Proceed to main activity even if image loading fails
            navigateToMainActivity();
        });
    }

    // Navigate to MainActivity with a delay
    private void navigateToMainActivity() {
        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }, 3000); // 3-second delay for splash screen
    }

    // Navigate to SetupActivity with a delay
    private void navigateToSetupActivity() {
        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, SetupActivity.class));
            finish();
        }, 3000); // 3-second delay for splash screen
    }

    // Initiate the Google Sign-In process
    private void signIn() {
        Intent intent = signInClient.getSignInIntent();
        startActivityForResult(intent, 100);
    }

    // Handle the result from Google Sign-In
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    Log.d("SplashActivity", "Google Sign-In successful. Account: " + account.getEmail());
                    AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                    auth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d("SplashActivity", "signInWithCredential:success");
                                checkUserProfileCompletion(auth.getCurrentUser().getUid());
                            } else {
                                Log.e("SplashActivity", "signInWithCredential:failure", task.getException());
                                Toast.makeText(SplashActivity.this, "Login Failed!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Log.e("SplashActivity", "GoogleSignInAccount is null.");
                    Toast.makeText(SplashActivity.this, "Login Failed!", Toast.LENGTH_SHORT).show();
                }
            } catch (ApiException e) {
                Log.e("SplashActivity", "Google Sign-In failed. Error code: " + e.getStatusCode(), e);
                if (e.getStatusCode() == GoogleSignInStatusCodes.NETWORK_ERROR) {
                    Toast.makeText(this, "Network error. Please try again.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Google Sign-In failed.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
