package com.jazzee.jazzeejobzz;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SettingsActivity extends AppCompatActivity {

    private EditText profileCategoryEdit, aboutEdit, otherDetailsEdit;
    private Button saveButton, clearButton;

    private FirebaseAuth auth;
    private DatabaseReference userRef;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);

        profileCategoryEdit = findViewById(R.id.profile_category_edit);
        aboutEdit = findViewById(R.id.about_edit);
        otherDetailsEdit = findViewById(R.id.other_details_edit);
        saveButton = findViewById(R.id.save_button);
        clearButton = findViewById(R.id.clear_button);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfileData();
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearProfileData();
            }
        });
    }

    private void saveProfileData() {
        String category = profileCategoryEdit.getText().toString();
        String about = aboutEdit.getText().toString();
        String otherDetails = otherDetailsEdit.getText().toString();

        userRef.child("profilecategory").setValue(category);
        userRef.child("about").setValue(about);
        userRef.child("otherdetails").setValue(otherDetails);

        Toast.makeText(SettingsActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
    }

    private void clearProfileData() {
        profileCategoryEdit.setText("");
        aboutEdit.setText("");
        otherDetailsEdit.setText("");
    }
}
