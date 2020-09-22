package com.example.cameraapp;

import android.content.Context;
import android.hardware.SensorManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.widget.Chronometer;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.cameraapp.MainActivity.myaccountName;

public class generateDataCSV {


    private static final Object SENSOR_SERVICE = 1;

    static double longitude = -1;
    static double latitude = -1;

    static long timeOfVid = 0;

    String[] datapoints;
    GPSTracker gps;
    SensorManager sensorManager;
    SensorData SD;
    LocationListener locationListener;
    LocationManager LM;


    File file;

    Uri photoURI;

    Context context;

    FilesSyncToFirebase filesSyncToFirebase;

    static long time = 0;

    generateDataCSV(Context con, File FileToWrite, SensorManager sm) {

        file = FileToWrite;
        filesSyncToFirebase = new FilesSyncToFirebase();
        sensorManager = sm;
        context = con;
        if (myaccountName != null) {
            filesSyncToFirebase.SetDirName(myaccountName.toString());
        } else {
            filesSyncToFirebase.SetDirName("unregistered");
            Toast.makeText(context.getApplicationContext(), "Please log in to enable Firebase storage", Toast.LENGTH_LONG).show();
        }
    }

    public void SetVidUri(Uri uri) {
        photoURI = uri;

    }

    public void stopRecording() {

        filesSyncToFirebase.StartSync();

    }

    public void startRecording() {

        gps = new GPSTracker(context);
        if (gps.canGetLocation()) {

            double lat = gps.getLatitude();
            double longi = gps.getLongitude();
            SD = new SensorData(sensorManager);
            datapoints = new String[9];
            int time = (int) (System.currentTimeMillis());
            Timestamp tsTemp = new Timestamp(time);
            String ts =  tsTemp.toString();
            datapoints[0] = ts;

            if (latitude != -1 && longitude != -1) {

                datapoints[1] = "" + latitude;
                datapoints[2] = "" + longitude;

            } else {
                datapoints[1] = "" + lat;
                datapoints[2] = "" + longi;
            }


            datapoints[3] = "gyro_x" + SensorData.gyro_x;
            datapoints[4] = "gyro_y" + SensorData.gyro_y;
            datapoints[5] = "gyro_x" + SensorData.gyro_z;

            datapoints[6] = "acc_x" + SensorData.linear_acc_x;
            datapoints[7] = "acc_y" + SensorData.linear_acc_y;
            datapoints[8] = "acc_z" + SensorData.linear_acc_z;


            boolean UpdateFile = false;


            int count = datapoints.length;

            for (int i = 0; i < datapoints.length; i++) {

                if (datapoints[i] == "-1") {

                    count--;
                }
            }
            if (count == 0) {
                UpdateFile = true;
                count = datapoints.length;

            }


            UpdateFile = true;

            if (UpdateFile) {

                try {
                    filesSyncToFirebase.StoreData(datapoints, file);
                    Log.i ("recording", datapoints.toString());


                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {

                Toast.makeText(context, "Working", Toast.LENGTH_LONG);
            }

            UpdateFile = false;

        } else {

            gps.showSettingsAlert();
        }


    }


}


