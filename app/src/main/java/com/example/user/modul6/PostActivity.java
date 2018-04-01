package com.example.user.modul6;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class PostActivity extends AppCompatActivity {

    //a constant to track the file chooser intent
    private static final int PICK_IMAGE_REQUEST = 234;

    EditText postDesc, postTitle;
    ImageView postPhoto;
    Button btnChoose;
    FloatingActionButton fabAddPost;

    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        FirebaseDatabase.getInstance().getReference("app_title").setValue("Popotoan");

        postTitle = findViewById(R.id.postTitle);
        postDesc = findViewById(R.id.postDescription);
        postPhoto = findViewById(R.id.postPhoto);
        btnChoose = findViewById(R.id.buttonChoose);
        fabAddPost = findViewById(R.id.fabAddPost);

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                        PICK_IMAGE_REQUEST);
            }
        });

        fabAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadPost();
            }
        });
    }

    private void uploadPost() {
        if (filePath != null) {
            String username = "";
            if (getIntent() != null) {
                username = getIntent().getStringExtra("username");
            }

            final ProgressDialog progressDialog = new ProgressDialog(PostActivity.this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();

            long millis = System.currentTimeMillis() % 1000;

            String title = postTitle.getText().toString();
            String desc = postDesc.getText().toString();
            String image = "images/"+ username +"_"+ millis +".jpg";

            // database
            DatabaseReference database = FirebaseDatabase.getInstance().getReference("posts");

            String postId = database.push().getKey();

            Post post = new Post();
            post.setUsername(username);
            post.setPhoto(image);
            post.setPhotoTitle(title);
            post.setPhotoDesc(desc);

            database.child(postId).setValue(post);

            // storage
            StorageReference riversRef = storageReference.child(image);
            riversRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();

                            Toast.makeText(getApplicationContext(),
                                    "File uploaded!", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(PostActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();

                            Toast.makeText(getApplicationContext(),
                                    e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.setMessage("Uploading...");
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(),
                    "No files!", Toast.LENGTH_SHORT).show();
        }
    }

    //handling the image chooser activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                postPhoto.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
