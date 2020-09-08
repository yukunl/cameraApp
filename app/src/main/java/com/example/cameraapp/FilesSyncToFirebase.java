package com.example.cameraapp;
import android.net.Uri;
import android.os.Environment;
import androidx.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.concurrent.Executor;


public class FilesSyncToFirebase {

    String [] DataPoints;
    String DirName;
    Uri photoUri;
    Uri File;
    boolean StoreFile;
    boolean StoreImage;
    private StorageReference mStorageRef;


    FilesSyncToFirebase(){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        mStorageRef = storage.getReference();

    }

/*    public void csvUploader(String filePath, final String callDate) {
        StorageReference mStorageReference = FirebaseStorage.getInstance().getReference();
        Log.e("LOG", "Entering CSVUPLOADER");
        Uri file = Uri.fromFile(new File(filePath));
        Log.e("csvUploader Uri File:", filePath.toString());

        // Create the file metadata
        StorageMetadata metadata = new StorageMetadata.Builder().setContentType("text/csv").build();
        Log.e("LOG","Metadata: " + metadata.toString());

        // Upload file and metadata to the path 'reports/date.csv'
        CancellableTask uploadTask = mStorageReference.child("reports/" + file.getLastPathSegment()).putFile(file, metadata);


        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                //System.out.println("Upload is " + progress + "% done");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.e("LOG", "Unsucessfull in CSVUPLOADER");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Handle successful uploads on complete
                //Uri downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
                //mainActivity.setDownloadLink(downloadUrl);
                Log.e("LOG", "Successfull in CSVUPLOADER");
                mainActivity.getUrlAsync(callDate);
            }
        });
    }*/


    public void StartSync(){

            StorageReference Data = mStorageRef.child(DirName+"/file.csv");
            DataSync(Data,File);
            DataSync(mStorageRef.child(DirName+"/vid.mp4"),photoUri);

    }



    private void  DataSync(StorageReference Data,Uri file) {
        final StorageReference DataRef = Data;

        Log.e("csvUploader dataref:", Data.toString());
        Log.e("csvUploader Uri File:", file.toString());

        Data.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //When the image has successfully uploaded, get its download URL
                        DataRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Uri dlUri = uri;
                                Log.e("csvUploader Uri File:", dlUri.toString());
                                        }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                        Log.e("csvUploader error:",   exception.getLocalizedMessage().toString());

                        Log.v("********************","*****************THIS SUCKS");

                    }
                });



        }


    public void  SetDirName(String name){

        DirName=name;
    }

    public void SetStoreFile(boolean t){

        StoreFile=t;
    }


    public void SetStoreImage(boolean t){

        StoreFile=t;
    }


    public void SetPhotoUri(Uri uri){
        photoUri=uri;
    }

    public Uri StoreData(String[] dataPoints,File file) throws IOException {


        String filePath=file.getAbsolutePath();

        FileOutputStream fileinput = new FileOutputStream(file,true);

        PrintStream printstream = new PrintStream(fileinput);

        for(int i=0;i<dataPoints.length;i++) {

            printstream.print(dataPoints[i]+",");

        }

        printstream.print("\n");

        fileinput.close();
        Log.e("csvUploader Uri File:", filePath.toString());
        return (File=Uri.fromFile(new File(filePath)));

    }

}
