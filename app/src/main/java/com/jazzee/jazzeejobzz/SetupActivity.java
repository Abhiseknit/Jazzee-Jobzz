package com.jazzee.jazzeejobzz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

public class SetupActivity extends AppCompatActivity {
    private EditText usernameofuser, countryofuser, fullnameofuser;
    private Button setupbutton;
    private ImageView profileicon;
    private ProgressDialog loadingBar;

    private FirebaseAuth auth;
    private DatabaseReference UsersRef;
    private StorageReference UserProfileIconRef;

    String currentUserID;
    final static int Gallery_Pick = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        auth = FirebaseAuth.getInstance();
        currentUserID = auth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        UserProfileIconRef = FirebaseStorage.getInstance().getReference().child("Profile Icon");

        usernameofuser = findViewById(R.id.usernameofuser);
        fullnameofuser = findViewById(R.id.fullnameofuser);
        countryofuser = findViewById(R.id.countryofuser);
        profileicon = findViewById(R.id.profileicon);
        setupbutton = findViewById(R.id.setupbutton);
        loadingBar = new ProgressDialog(this);

        setupbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveAccountSetupInformation();
            }
        });

        profileicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, Gallery_Pick);
            }
        });

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("profileicon")) {
                        String image = dataSnapshot.child("profileicon").getValue().toString();
                        Picasso.get().load(image).placeholder(R.drawable.profile).into(profileicon);
                    } else {
                        Toast.makeText(SetupActivity.this, "Please select a profile image first.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            String destinationUri = UUID.randomUUID().toString() + ".jpg";

            UCrop.of(imageUri, Uri.fromFile(new File(getCacheDir(), destinationUri)))
                    .withAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            final Uri resultUri = UCrop.getOutput(data);

            loadingBar.setTitle("Profile Icon");
            loadingBar.setMessage("Please wait, while we update your profile icon");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            StorageReference filePath = UserProfileIconRef.child(currentUserID + ".jpg");

            filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(SetupActivity.this, "Profile Image stored successfully to Firebase storage.", Toast.LENGTH_SHORT).show();

                        filePath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    final String downloadUrl = task.getResult().toString();

                                    UsersRef.child("profileicon").setValue(downloadUrl)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Intent selfIntent = new Intent(SetupActivity.this, SetupActivity.class);
                                                        startActivity(selfIntent);

                                                        Toast.makeText(SetupActivity.this, "Profile Image stored to Firebase Database Successfully...", Toast.LENGTH_SHORT).show();
                                                        loadingBar.dismiss();
                                                    } else {
                                                        String message = task.getException().getMessage();
                                                        Toast.makeText(SetupActivity.this, "Error Occurred: " + message, Toast.LENGTH_SHORT).show();
                                                        loadingBar.dismiss();
                                                    }
                                                }
                                            });
                                } else {
                                    String message = task.getException().getMessage();
                                    Toast.makeText(SetupActivity.this, "Error Occurred: " + message, Toast.LENGTH_SHORT).show();
                                    loadingBar.dismiss();
                                }
                            }
                        });
                    }
                }
            });
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
            Toast.makeText(this, "Error: " + cropError.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void SaveAccountSetupInformation() {
        String username = usernameofuser.getText().toString();
        String fullname = fullnameofuser.getText().toString();
        String country = countryofuser.getText().toString();

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Please write your username.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(fullname)) {
            Toast.makeText(this, "Please write your full name.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(country)) {
            Toast.makeText(this, "Please write your country.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if profile icon is uploaded
        UsersRef.child("profileicon").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Toast.makeText(SetupActivity.this, "Please upload a profile image.", Toast.LENGTH_SHORT).show();
                    return;
                }

                loadingBar.setTitle("Saving Information");
                loadingBar.setMessage("Please wait, while we are creating your new Account.");
                loadingBar.show();
                loadingBar.setCanceledOnTouchOutside(true);

                HashMap<String, String> userMap = new HashMap<>();
                userMap.put("username", username);
                userMap.put("fullname", fullname);
                userMap.put("country", country);

                UsersRef.updateChildren((HashMap) userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            SendUserToMainActivity();
                            Toast.makeText(SetupActivity.this, "Your Account is created Successfully.", Toast.LENGTH_LONG).show();
                        } else {
                            String message = task.getException().getMessage();
                            Toast.makeText(SetupActivity.this, "Error Occurred: " + message, Toast.LENGTH_SHORT).show();
                        }
                        loadingBar.dismiss();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SetupActivity.this, "Error checking profile icon: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(SetupActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}
