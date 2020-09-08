package com.example.cameraapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import	android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.CamcorderProfile;
import android.media.Image;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;


public class MainActivity extends AppCompatActivity implements SensorEventListener,View.OnClickListener{

    private static final String TAG = "MainActivity";
    private SensorManager sensorManageracc;
    private SensorManager sensorManagergyro;
    TextView xvalue, yvalue, zvalue, gyrox, gyroy,gyroz;
    private LineChart mChart;
    private Thread thread;
    private boolean plotData = true;
    Sensor accelerometer;
    Sensor gyroscope;

    //real time

    private Camera mCamera;
    private CameraPreview mPreview;
    TimedDataRecording timedDataRecording;
    GPSTracker gps;
    private SensorManager sensorManager;
    Camera.Parameters params;
    private MediaRecorder mMediaRecorder;
    File DataFile;
    private boolean isRecording = false;
    Chronometer chrono;

    // ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        //camera
        Button backCamera = findViewById(R.id.backcamera);

        backCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, 1);
                }
            }

        });


        //camera
        Button frontCamera = findViewById(R.id.frontcamera);

        frontCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, 1);
                }
            }

        });



        //accelerometer
        sensorManageracc = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        accelerometer = sensorManageracc.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            sensorManageracc.registerListener(MainActivity.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }

        //gyroscope
        sensorManagergyro = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gyroscope = sensorManagergyro.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if (gyroscope != null) {
            sensorManageracc.registerListener(MainActivity.this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        }


        mChart = (LineChart) findViewById(R.id.accChart);
        mChart.getDescription().setEnabled(true);
        mChart.getDescription().setText("Real Time Accelerometer Data Plot");
        mChart.setTouchEnabled(false);
        mChart.setDragEnabled(false);
        mChart.setDrawGridBackground(false);
        mChart.setPinchZoom(false);


        mChart.setBackgroundColor(Color.WHITE);
        LineData data = new LineData();
        data.setValueTextColor(Color.BLACK);
        mChart.setData(data);

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.BLACK);

        XAxis xl = mChart.getXAxis();
        xl.setTextColor(Color.BLACK);
        xl.setDrawGridLines(true);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMaximum(10f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);

        mChart.getAxisLeft().setDrawGridLines(false);
        mChart.getXAxis().setDrawGridLines(false);
        mChart.setDrawBorders(false);

        feedMultiple();

        //for textview data
        xvalue = (TextView) findViewById(R.id.xValue);
        yvalue = (TextView) findViewById(R.id.yValue);
        zvalue = (TextView) findViewById(R.id.zValue);

        //for textview data
        gyrox = (TextView) findViewById(R.id.gyroxValue);
        gyroy = (TextView) findViewById(R.id.gyroyValue);
        gyroz = (TextView) findViewById(R.id.gyrozValue);

        startPlot ();

        //real time
        MarshMelloPermission marshMelloPermission =new MarshMelloPermission(this);
        if(!marshMelloPermission.checkPermissionForRecord()){
            marshMelloPermission.checkPermissionForRecord();
        }
        if(!marshMelloPermission.checkPermissionForCamera()){
            marshMelloPermission.checkPermissionForCamera();
        }
        if(!marshMelloPermission.checkPermissionForExternalStorage()){
            marshMelloPermission.checkPermissionForExternalStorage();
        }

        if(!marshMelloPermission.checkPermissionForFineLocation()){
            marshMelloPermission.checkPermissionForFineLocation();
        }
        chrono = (Chronometer) this.findViewById(R.id.chrono);
        final long clock=SystemClock.elapsedRealtime();
        final int[] i = {0};

        try {
            //  FrameLayout preview = (FrameLayout) findViewById(R.id.CameraPreview);
            //    preview.addView(mPreview);
            mCamera=getCameraInstance();
            mPreview= new CameraPreview(MainActivity.this,mCamera);

        } catch (Exception e) {
            e.printStackTrace();
        }

        FrameLayout preview = (FrameLayout) findViewById(R.id.CameraPreview);
        preview.addView(mPreview);
        sensorManager= (SensorManager) getSystemService(Context.SENSOR_SERVICE);


        preview.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (isRecording) {
                            // stop recording and release camera
                            mMediaRecorder.stop();  // stop the recording
                            releaseMediaRecorder(); // release the MediaRecorder object
                            mCamera.lock();         // take camera access back from MediaRecorder
                            chrono.stop();
                            timedDataRecording.stopTimer();
                            // inform the user that recording has stopped
                            isRecording = false;
                        } else {
                            // initialize video camera
                            if (prepareVideoRecorder()) {
                                // Camera is available and unlocked, MediaRecorder is prepared,
                                // now you can start recording
                                mMediaRecorder.start();
//                                  chrono.setBase(clock);
                                chrono.start();
                                File file=null;

                                try {
                                    file=createFile();
                                } catch (IOException e) {
                                    e.printStackTrace();

                                }

                                timedDataRecording= new TimedDataRecording(MainActivity.this,file,sensorManager);
                                timedDataRecording.SetVidUri(getOutputMediaFileUri(MEDIA_TYPE_VIDEO));
                                timedDataRecording.startTimer(chrono);
                                isRecording = true;
                            } else {
                                // prepare didn't work, release the camera
                                releaseMediaRecorder();
                                // inform user
                            }
                        }
                    }
                }
        );

    }

    private void startPlot () {

        if (thread != null) {
            thread.interrupt();
        }
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    plotData = true;
                    try {
                        Thread.sleep( 10);
                    }catch ( InterruptedException e ) { e.printStackTrace();}
                }
            }
        });
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        Log.d("Accelerometer", "onSensorChanged x: " + event.values[0] + "Y: " + event.values[1] + "Z: " + event.values[2]);
        xvalue.setText("X value: " + event.values[0]);
        yvalue.setText("Y value: " + event.values[1]);
        zvalue.setText("Z value: " + event.values[2]);

        Log.d("Gyroscope", "onSensorChanged x: " + event.values[0] + "Y: " + event.values[1] + "Z: " + event.values[2]);
        gyrox.setText("X value: " + event.values[0]);
        gyroy.setText("Y value: " + event.values[1]);
        gyroz.setText("Z value: " + event.values[2]);

        if (plotData) {
            addEntry(event);
            plotData = false;
        }

        // Write a message to the database

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String emailinfo = getIntent().getExtras().toString();
        final DatabaseReference myRef = database.getReference("User:/"+ emailinfo);
        final String tempacc = event.values[0] + "Y: " + event.values[1] + "Z: " + event.values[2];
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "real time: " + "prepare");
               /* new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                Log.d(TAG, "real time: " + "prepare to write real time");
                                myRef.child("points").push().setValue(tempacc);
                            }
                        },0,
                        1000 * 15);*/
                final DatabaseReference temppoints = myRef.child("points").push();
                new Timer().scheduleAtFixedRate(new TimerTask() {

                    @Override
                    public void run() {
                        Log.d(TAG, "real time: " + "prepare to write real time");
                        temppoints.setValue(tempacc);
                    }
                },0, 1000 * 60);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void addEntry(SensorEvent event) {

        LineData data = mChart.getData();

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);
            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

            data.addEntry(new Entry(set.getEntryCount(), event.values[0] + 5), 0);
            data.notifyDataChanged();

            // let the chart know it's data has changed
            mChart.notifyDataSetChanged();

            // limit the number of visible entries
            mChart.setVisibleXRangeMaximum(150);
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            mChart.moveViewToX(data.getEntryCount());

        }
    }


    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "Dynamic Data");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(3f);
        set.setColor(Color.MAGENTA);
        set.setHighlightEnabled(false);
        set.setDrawValues(false);
        set.setDrawCircles(false);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);
        return set;
    }

    private void feedMultiple() {

        if (thread != null) {
            thread.interrupt();
        }

        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    plotData = true;
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (thread != null) {
            thread.interrupt();
        }
        sensorManageracc.unregisterListener(this);
        releaseMediaRecorder();       // if you are using MediaRecorder, release it first
        releaseCamera();              // release the camera immediately on pause event


    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManageracc.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
// Get the Camera instance as the activity achieves full user focus
        if (mCamera == null) {
            mCamera=getCameraInstance();
        }

        if(mMediaRecorder==null){
            prepareVideoRecorder();
        }
    }

    public Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)

            Toast.makeText(getApplicationContext(),"Camera is not available (in use or does not exist)",Toast.LENGTH_SHORT);
        }
        return c; // returns null if camera is unavailable
    }

    private File createFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String FileName = "CSV_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.getExternalStorageState());



        File file = File.createTempFile(
                FileName,  /* prefix */
                ".csv",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        return file;
    }

    private boolean prepareVideoRecorder(){

        mCamera = getCameraInstance();
        mMediaRecorder = new MediaRecorder();

        // Step 1: Unlock and set camera to MediaRecorder
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);

        // Step 2: Set sources
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

        // Step 4: Set output file
        mMediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString());

        // Step 5: Set the preview output
        mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());

        // Step 6: Prepare configured MediaRecorder
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    private void releaseMediaRecorder(){
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();   // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
            mCamera.lock();           // lock camera for later use
        }
    }

    private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }

    private  Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = getExternalFilesDir(Environment.getExternalStorageState());
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.



        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }


    @Override
    protected void onDestroy() {
        sensorManageracc.unregisterListener(this);
        thread.interrupt();
        super.onDestroy();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mymenu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.login:
                Intent intent = new Intent(this, keystroke.class);
                this.startActivity(intent);

                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    public void onClick(View view) {

    }



}