package com.tschuchort.soundshare.soundshare;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

/**
 * Created by jvollmer on 26.11.2017.
 */

public class RecordingActivity extends AppCompatActivity {

    private static final String TAG = RecordingActivity.class.getSimpleName();
    private static final int PICKFILE_REQUEST_CODE = 1234 ;
    private static final int ACTIVITY_RECORD_SOUND = 4321;
    private Button record;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);

        record = findViewById(R.id.record);
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                startActivityForResult(intent, ACTIVITY_RECORD_SOUND);
            }
        });
        Button upload = findViewById(R.id.upload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/*");
                startActivityForResult(intent, PICKFILE_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode, Intent data){
        Log.i(TAG, TAG+"onActivityResult requestCode "+requestCode);
        if(requestCode == PICKFILE_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                //the selected audio.
                Uri uri = data.getData();
                Log.i(TAG, TAG+"onActivityResult "+uri.toString());
                upload(uri);
            }
        }
        if(requestCode == ACTIVITY_RECORD_SOUND){
            if(resultCode == RESULT_OK){
                //the recorded audio.
                Uri uri = data.getData();

                Log.i(TAG, TAG+"onActivityResult "+uri.toString());
                upload(uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void upload(Uri file) {

        // Create a storage reference from our app
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        StorageReference riversRef = storageRef.child("Soundfiles/"+file.getLastPathSegment());
        UploadTask uploadTask = riversRef.putFile(file);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.i(TAG, TAG+"upload onFailure");

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();

                Log.i(TAG, TAG+"upload onSuccess");
            }
        });
    }
}
