package com.ductai.course.firebasestorage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    Button btnDownload, btnUpload, btnDelete, btnLogout;
    TextView txtDownStatus;
    ImageView imgView;
    FirebaseAuth auth;
    ProgressBar progressBar;
    StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.prgbar);
        btnDownload = findViewById(R.id.btn_download);
        btnUpload = findViewById(R.id.btn_upload);
        btnDelete = findViewById(R.id.btn_delete);
        txtDownStatus = findViewById(R.id.txt_download_status);
        imgView = findViewById(R.id.img_downloaded);
        btnLogout = findViewById(R.id.btn_logout);
        auth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        View.OnClickListener btnClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.btn_download:
                        downloadFile();
                        break;
                    case R.id.btn_upload:
                        upLoadFile();
                        break;
                    case R.id.btn_delete:
                        break;
                    case R.id.btn_logout:
                        auth.signOut();
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                }
            }
        };

        btnLogout.setOnClickListener(btnClick);
        btnDelete.setOnClickListener(btnClick);
        btnUpload.setOnClickListener(btnClick);
        btnDownload.setOnClickListener(btnClick);
    }

    private void upLoadFile(){
        progressBar.setVisibility(View.VISIBLE);

        StorageReference fileRef = storageReference.child("images").child("my_img.png");
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.sha1);
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                toast("Upload file successful");
                progressBar.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                toast(e.getMessage());
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void downloadFile(){
        progressBar.setVisibility(View.VISIBLE);

        StorageReference fileRef = storageReference.child("images").child("my_img.png");
        File folder = new File(getExternalFilesDir(null).getAbsolutePath() + "/FirebaseStorage");
        if(!folder.exists()){
            folder.mkdir();
        }

        try {
            final File file = File.createTempFile("images", ".png", folder);
            fileRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    imgView.setImageBitmap(bitmap);
                    progressBar.setVisibility(View.GONE);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                    toast(e.getMessage());
                }
            });
        } catch (IOException e) {
            progressBar.setVisibility(View.GONE);
            toast(e.getMessage());
            e.printStackTrace();
        }

    }

    private void toast(String message){
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}