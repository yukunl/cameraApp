package com.example.cameraapp;

import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;



import androidx.appcompat.app.AppCompatActivity;

public class keystroke extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.keyboard);
        Intent intent = getIntent();
        EditText message = (EditText)findViewById(R.id.message);
        message.setOnEditorActionListener(new TextView.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Log.i("key pressed", String.valueOf(event.getKeyCode()));
               // onKeyDown(event.getKeyCode(), event);
                return true;
            }




        });
    }

//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        Log.i("This is the key pressed", String.valueOf(event.getKeyCode()));
//        return true;
//    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (KeyEvent.KEYCODE_BACK == keyCode){
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
//
//    @Override
//    public boolean dispatchKeyEvent(KeyEvent event) {
//        Log.i("key pressed", String.valueOf(event.getKeyCode()));
//        return super.dispatchKeyEvent(event);
//    }




}
