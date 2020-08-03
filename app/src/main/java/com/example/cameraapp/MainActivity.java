package com.example.cameraapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
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
    //firebase check
    private Button register;
    private EditText email;
    private EditText password;
    private TextView signin;
    private String emailinfo;
    private String passwordinfo;
    private ProgressDialog progressDialog;
  //  private FirebaseAuth firebaseAuth;


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

        // keystroke
        Button keystroke = findViewById(R.id.keystroke);
        keystroke.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, keystroke.class);
                startActivity(intent);
            }
        }

        );

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

        //get email and password
        register = (Button) findViewById(R.id.register);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        signin = (TextView) findViewById(R.id.signin);
        progressDialog = new ProgressDialog (this);
        progressDialog.setMessage("Registering user");
        progressDialog.show();
        register.setOnClickListener(this);
        signin.setOnClickListener(this);
      /*  firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(emailinfo, passwordinfo).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(MainActivity.this, "Registered Successfully", Toast.LENGTH_SHORT);
                }
                else {

                    Toast.makeText(MainActivity.this, "Please try again", Toast.LENGTH_SHORT);

                }
                       }
        });*/
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManageracc.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);

    }

    @Override
    protected void onDestroy() {
        sensorManageracc.unregisterListener(this);
        thread.interrupt();
        super.onDestroy();
    }

    private void registerUser () {
         emailinfo = email.getText().toString().trim();
         passwordinfo = password.getText().toString().trim();
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        if (TextUtils.isEmpty(emailinfo)) {
              Toast.makeText(context, "Please enter email address", duration).show();
        }
        if (TextUtils.isEmpty(passwordinfo)) {
            Toast.makeText(context, "Please enter password", duration).show();
        }

    }
    @Override
    public void onClick(View view) {
        if (view ==register) {
            registerUser ();
        }
        if (view ==signin) {
            //open login activity
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data){
//        super.onActivityResult(requestCode, resultCode, data);
//        Bitmap bitmap = (Bitmap)data.getExtras().get("data");
//       // imageView.setImageBitmap(bitmap);
//    }


}
