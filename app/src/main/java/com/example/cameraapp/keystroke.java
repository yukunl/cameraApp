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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class keystroke extends AppCompatActivity implements View.OnClickListener{

    private Button register;
    private EditText email;
    private EditText password;
    private TextView signin;
    private String emailinfo;
    private String passwordinfo;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private MyKeyboard keyboard;
    private InputConnection ic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.keystrokeview);
        Intent intent = getIntent();


         email = (EditText) findViewById(R.id.email);
         password = (EditText) findViewById(R.id.password);
         keyboard = (MyKeyboard) findViewById(R.id.keyboard);


        registerEditText(R.id.email);
        registerEditText(R.id.password);



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
        }
    }


    // retrieved from website : http://www.fampennings.nl/maarten/android/09keyboard/index.htm
    public void registerEditText(int resid) {
        // Find the EditText 'resid'
        EditText edittext= (EditText)findViewById(resid);
        edittext.setOnTouchListener(new View.OnTouchListener() {
            @Override public boolean onTouch(View v, MotionEvent event) {
                EditText edittext = (EditText) v;
               // int inType = edittext.getInputType();       // Backup the input type
                edittext.setRawInputType(InputType.TYPE_CLASS_TEXT); // Disable standard keyboard
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
        progressDialog.setMessage("Registering User");
        progressDialog.show();

        firebaseAuth = FirebaseAuth.getInstance();

         /*  firebaseAuth.createUserWithEmailAndPassword(emailinfo, passwordinfo)
             .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                         if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                          //  FirebaseUser user = firebaseAuth.getCurrentUser();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });*/

        firebaseAuth.createUserWithEmailAndPassword(emailinfo, passwordinfo)
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
                });

        firebaseAuth.createUserWithEmailAndPassword(emailinfo, passwordinfo).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){
                    Toast.makeText(keystroke.this, "Registered Successfully", Toast.LENGTH_SHORT);
                }
                else {

                    Toast.makeText(keystroke.this, "Please try again", Toast.LENGTH_SHORT);
                }
            }
        });

    }


}




