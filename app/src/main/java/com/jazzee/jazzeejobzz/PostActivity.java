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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

public class PostActivity extends AppCompatActivity {

    private ImageView SelectPostImage;
    private Button UpdatePostButton;
    private EditText PostDescription;
    private ProgressDialog loadingBar;

    private Uri ImageUri;
    private String Description;
    private StorageReference PostImagesReference;
    private DatabaseReference UsersRef, PostsRef;
    private FirebaseAuth auth;
    private String current_user_id;
    private static final int Gallery_Pick = 1;
    private String postId;
    private String downloadUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        auth = FirebaseAuth.getInstance();
        current_user_id = auth.getCurrentUser().getUid();

        PostImagesReference = FirebaseStorage.getInstance().getReference();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);
        PostsRef = FirebaseDatabase.getInstance().getReference().child("posts").child(current_user_id);

        SelectPostImage = findViewById(R.id.post_image_view);
        UpdatePostButton = findViewById(R.id.upload_button);
        PostDescription = findViewById(R.id.caption_edit_text);
        loadingBar = new ProgressDialog(this);

        SelectPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, Gallery_Pick);
            }
        });

        UpdatePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidatePostInfo();
            }
        });
    }

    private void ValidatePostInfo() {
        Description = PostDescription.getText().toString();
        postId = PostsRef.push().getKey();  // Generate postId early

        if (ImageUri == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(Description)) {
            Toast.makeText(this, "Please write a description", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.show();
            StoreImageToFirebaseStorage();
        }
    }

    private void StoreImageToFirebaseStorage() {
        StorageReference filePath = PostImagesReference.child("Post Images").child(current_user_id).child(ImageUri.getLastPathSegment() + postId + ".jpg");

        filePath.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    filePath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                downloadUrl = task.getResult().toString();
                                Toast.makeText(PostActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                                SavingPostInformationToDatabase();
                            } else {
                                String message = task.getException().getMessage();
                                Toast.makeText(PostActivity.this, "Error occurred: " + message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
                } else {
                    String message = task.getException().getMessage();
                    Toast.makeText(PostActivity.this, "Error occurred: " + message, Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }
        });
    }

    private void SavingPostInformationToDatabase() {
        if (postId != null && downloadUrl != null) {
            HashMap<String, Object> postMap = new HashMap<>();
            postMap.put("description", Description);
            postMap.put("image", downloadUrl);
            postMap.put("uid", current_user_id);
            postMap.put("timestamp", System.currentTimeMillis());

            PostsRef.child(postId).updateChildren(postMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(PostActivity.this, "Post updated successfully", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();

                        Intent mainIntent = new Intent(PostActivity.this, MainActivity.class);
                        startActivity(mainIntent);
                        finish();
                    } else {
                        String message = task.getException().getMessage();
                        Toast.makeText(PostActivity.this, "Error occurred: " + message, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            });
        } else {
            Toast.makeText(this, "Error: Post ID or Download URL is null", Toast.LENGTH_SHORT).show();
            loadingBar.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null) {
            ImageUri = data.getData();
            String destinationUri = UUID.randomUUID().toString() + ".jpg";

            UCrop.of(ImageUri, Uri.fromFile(new File(getCacheDir(), destinationUri)))
                    .withAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            final Uri resultUri = UCrop.getOutput(data);

            loadingBar.setTitle("Post Image");
            loadingBar.setMessage("Please wait, while we are uploading your post image");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            StorageReference filePath = PostImagesReference.child("Post Images").child(current_user_id).child(resultUri.getLastPathSegment() + postId + ".jpg");

            filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        filePath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    downloadUrl = task.getResult().toString();
                                    Toast.makeText(PostActivity.this, "Post Image stored successfully to Firebase storage.", Toast.LENGTH_SHORT).show();
                                    SavingPostInformationToDatabase();
                                } else {
                                    String message = task.getException().getMessage();
                                    Toast.makeText(PostActivity.this, "Error occurred: " + message, Toast.LENGTH_SHORT).show();
                                    loadingBar.dismiss();
                                }
                            }
                        });
                    } else {
                        String message = task.getException().getMessage();
                        Toast.makeText(PostActivity.this, "Error occurred: " + message, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            });
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
            Toast.makeText(this, "Error: " + cropError.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
