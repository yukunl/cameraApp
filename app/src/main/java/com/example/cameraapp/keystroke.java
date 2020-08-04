package com.example.cameraapp;

import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class keystroke extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.keyboard);
        Intent intent = getIntent();
    }

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
