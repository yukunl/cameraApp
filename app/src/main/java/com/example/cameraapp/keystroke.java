package com.example.cameraapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class keystroke extends AppCompatActivity implements View.OnClickListener{

    private Button register;
    private EditText email;
    private EditText password;
    private TextView signin;
    private String emailinfo;
    private String passwordinfo;
    private ProgressBar progressBar;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private FirebaseAuth firebaseAuth;
    private MyKeyboard keyboard;
    private InputConnection ic;
    public static Boolean MyaccountShow = false;
    public static String userID = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.keystrokeview);
        Intent intent = getIntent();
        mAuth = FirebaseAuth.getInstance();

         email = (EditText) findViewById(R.id.email);
         password = (EditText) findViewById(R.id.password);
         keyboard = (MyKeyboard) findViewById(R.id.keyboard);


        registerEditText(R.id.email);
        registerEditText(R.id.password);
        progressBar = findViewById(R.id.progressBar);


        //get email and password
        register = (Button) findViewById(R.id.register);
        registerUser ();
        signin = (TextView) findViewById(R.id.signin);

        register.setOnClickListener(this);
        signin.setOnClickListener(this);

        progressDialog = new ProgressDialog (this);

    }

    @Override
    public void onClick(View view) {
        if (view ==register) {
            registerUser ();
        }
        if (view ==signin) {
            //open login activity
            userLogin();
        }
    }
    private void userLogin(){
        emailinfo = email.getText().toString().trim();
        passwordinfo = password.getText().toString().trim();

        if (TextUtils.isEmpty(emailinfo)){
            //username is empty
            Toast.makeText(this, "Please enter username", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(passwordinfo)){
            //password is empty
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
        }

        progressDialog.setMessage("Logging in user");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(emailinfo, passwordinfo)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "Login successful!", Toast.LENGTH_LONG).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                             MyaccountShow = true;
                            invalidateOptionsMenu();
                            finish();
                            Intent intent= new Intent(keystroke.this,MainActivity.class);
                            intent.putExtra("emailinfo", emailinfo);
                            startActivity(intent);                         }
                    }
                });

        // myRef.push().setValue(emailinfo);
      //  myRef.setValue(emailinfo).push ();
    //    final DatabaseReference temppoints = myRef.child("points").push();

    }


    // retrieved from website : http://www.fampennings.nl/maarten/android/09keyboard/index.htm
    public void registerEditText(int resid) {
        // Find the EditText 'resid'
        EditText edittext= (EditText)findViewById(resid);
//        edittext.setOnTouchListener(new View.OnTouchListener() {
//            @Override public boolean onTouch(View v, MotionEvent event) {
//                EditText edittext = (EditText) v;
//              // int inType = edittext.getInputType();       // Backup the input type
//                edittext.setRawInputType(InputType.TYPE_CLASS_TEXT); // Disable standard keyboard
//                edittext.setTextIsSelectable(true);               // Call native handler
//                ic = edittext.onCreateInputConnection(new EditorInfo());
//                keyboard.setInputConnection(ic);// Restore input type
//                return true; // Consume touch event
//            }
//        });
        edittext.setOnTouchListener(new View.OnTouchListener(){
            @Override public boolean onTouch(View v, MotionEvent event) {
                EditText edittext = (EditText) v;
                int inType = edittext.getInputType();       // Backup the input type
                edittext.setInputType(InputType.TYPE_NULL); // Disable standard keyboard
                edittext.onTouchEvent(event);               // Call native handler
                edittext.setInputType(inType);              // Restore input type
                edittext.setTextIsSelectable(true);               // Call native handler
               ic = edittext.onCreateInputConnection(new EditorInfo());
                keyboard.setInputConnection(ic);// Restore input type
                return true; // Consume touch event
            }
        });



        // Disable spell check (hex strings look like words to Android)
        edittext.setInputType( edittext.getInputType() | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS );
    }

    private void registerUser () {
        progressBar.setVisibility(View.VISIBLE);
        emailinfo = email.getText().toString().trim();
        passwordinfo = password.getText().toString().trim();
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        if (TextUtils.isEmpty(emailinfo)) {
            Toast.makeText(context, "Please enter email address", duration).show();
            return;
        }
        if (TextUtils.isEmpty(passwordinfo)) {
            Toast.makeText(context, "Please enter password", duration).show();
            return;
        }
//        progressDialog.setMessage("Registering User");
//        progressDialog.show();

        firebaseAuth = FirebaseAuth.getInstance();


        mAuth.createUserWithEmailAndPassword(emailinfo, passwordinfo)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Write a message to the database

                            FirebaseDatabase database = FirebaseDatabase.getInstance();

                            final DatabaseReference myRef = database.getReference();
                            String emailName = emailinfo.split("@")[0];
                            Log.d("firebase" , emailName);
                            User user = new User(emailName, emailinfo, passwordinfo);
                            DatabaseReference usersRef = myRef.child("Users");

                            //add user object under the parent "Users"
                            //usersRef.push().setValue(user);
                            FirebaseUser mCurrentUser= task.getResult().getUser();
                             String userid =mCurrentUser.getUid();
                            usersRef.child(userid).setValue(user);
                            Log.d("firebase" , userid);
                            //Add sub children (sensor) to user node
                            DatabaseReference sensor_ref =usersRef.child(userid).child("Sensors");
                            HashMap <String, KeystrokeData> KeystrokeArray = MyKeyboard.getKeystrokeArray ();
                            sensor_ref.child("Keystrokes").setValue( KeystrokeArray);
                            sensor_ref.child("Accelerometer").setValue(0);
                            sensor_ref.child("Camera").setValue(0);

                            Toast.makeText(getApplicationContext(), "Registration successful!", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                            userID = userid;
                            //Intent intent = new Intent(keystroke.this, keystroke.class);
                            //startActivity(intent);
                        }
                        else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(keystroke.this, "User with this email already exist.", Toast.LENGTH_SHORT).show();
                            }

                            Toast.makeText(getApplicationContext(), "Registration failed! Please try again later", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }

                });





      /*  firebaseAuth.createUserWithEmailAndPassword(emailinfo, passwordinfo)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(keystroke.this, "Registered Successfully", Toast.LENGTH_SHORT);

                        } else {


                            // If sign in fails, display a message to the user.
                            Toast.makeText(keystroke.this, "Please try again", Toast.LENGTH_SHORT);
                        }

                        // ...
                    }
                });*/



    }

public String getEmailinfo () {return emailinfo;}
}




