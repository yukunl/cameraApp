package com.example.cameraapp;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class SensorData implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mLight;

    static  float linear_acc_x = -1;
    static float linear_acc_y = -1;
    static float linear_acc_z = -1;


    static float gyro_x = -1;
    static float gyro_y = -1;
    static float gyro_z = -1;

    SensorData(SensorManager sm) {
        mSensorManager =sm;
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),  SensorManager.SENSOR_DELAY_NORMAL);
        Log.i("changeabc", "reg acc");
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_NORMAL);
        Log.i("changeabc", "reg gyr");
          }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            Log.i("changeabc", "enter gyro");
            gyro_x = event.values[0];
            gyro_y = event.values[1];
            gyro_z = event.values[2];
        }
        else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            Log.i("changeabc", "enter acc");

            linear_acc_x = event.values[0];
            linear_acc_y = event.values[1];
            linear_acc_z = event.values[2];
        }


    }


    String[] GetGyro(){

        String[] gy= new String[3];

        gy[0]=""+gyro_x;
        gy[1]=""+gyro_y;
        gy[2]=""+gyro_z;

        return gy;
    }




    String[] GetAccel(){

        String[] gy= new String[3];

        gy[0]=""+linear_acc_x;
        gy[1]=""+linear_acc_y;
        gy[2]=""+linear_acc_z;

        return gy;
    }

}
