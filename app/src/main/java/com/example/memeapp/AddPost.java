package com.example.memeapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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

public class AddPost extends AppCompatActivity {

    private static int GALLERY_REQUEST =1;
    private Button btn_submit;
    private ImageButton btn_addpost;
    private EditText et;
    private Uri imageuri = null;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;
    private  FirebaseStorage storage;
    private FirebaseAuth firebaseAuth;
    public  String imageurl;
    public  String username;
    public  String currentuser,caption_val;
    StorageReference filepath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        btn_addpost= (ImageButton) findViewById(R.id.btn_addpost);
        btn_submit= (Button)findViewById(R.id.btn_submit);
        et = (EditText) findViewById(R.id.et_caption);
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
//        storage = FirebaseStorage.getInstance();
        currentuser= firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("blog_images");
        storageReference = FirebaseStorage.getInstance().getReference();
        btn_addpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryintent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryintent.setType("image/*");
                startActivityForResult(galleryintent,GALLERY_REQUEST);
            }
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("uploading post...");
                progressDialog.show();
                startposting();
            }
        });

    }

    public void startposting(){

        caption_val = et.getText().toString().trim();

        if(caption_val.equals(null) || imageuri.equals(null)){

            Toast.makeText(AddPost.this,"all fields are mendatory..",Toast.LENGTH_SHORT).show();
            //show something wrong
        }else {

            username = getUsername();
            filepath  = storageReference.child("blog images").child(imageuri.getLastPathSegment());


            UploadTask uploadtask = filepath.putFile(imageuri);
            Task<Uri> urlTask = uploadtask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return filepath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        imageurl = downloadUri.toString();
                        progressDialog.dismiss();
//                   String imageurl = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                        DatabaseReference temp1= databaseReference.push();
                        temp1.child("caption").setValue(caption_val);
                        temp1.child("username").setValue(username);
                        temp1.child("image").setValue(imageurl);
                        Toast.makeText(AddPost.this,"post uploaded successfully..",Toast.LENGTH_SHORT).show();
//
                        Intent i = new Intent(AddPost.this,Feed.class);
                        finish();
                        startActivity(i);
                    } else {
                        Toast.makeText(AddPost.this,"error for getting image url ",Toast.LENGTH_SHORT).show();
                        // Handle failures
                        // ...
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    String exception = e.getMessage().toString();
                    Toast.makeText(AddPost.this,exception,Toast.LENGTH_LONG).show();
                }
            });
//
        }

    }

    protected String getUsername() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("user info").child(currentuser);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                username = dataSnapshot.child("name").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return username;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        imageuri = data.getData();

        btn_addpost.setImageURI(imageuri);
        Toast.makeText(AddPost.this,"The Image Will Be Uploaded As It is!!!",Toast.LENGTH_LONG).show();
        super.onActivityResult(requestCode, resultCode, data);
    }
}


