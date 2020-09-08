package com.example.cameraapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyKeyboard extends LinearLayout implements View.OnClickListener {

    // constructors
    public MyKeyboard(Context context) {
        this(context, null, 0);
    }

    public MyKeyboard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyKeyboard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    // keyboard keys (buttons)
    private Button mButton1, mButton2, mButton3,mButton4, mButton5, mButton6,
            mButton7, mButton8, mButton9,mButton0, mButtonq, mButtonw,
            mButtone, mButtonr, mButtont,mButtony, mButtonu, mButtoni, mButtono,
            mButtonp, mButtona,mButtons, mButtond, mButtonf, mButtong, mButtonh, mButtonshift,
            mButtonj,mButtonk, mButtonl, mButtonz,mButtonx, mButtonc,mButtonv, mButtonb, mButtonn,mButtonm;
    private Button mButtonDelete, mButtonSpace, mButtonAt, mButtonDash, mButtonDot;
    private Button mButtonEnter;
    private boolean isUpper = false;
    private Context context;

    // This will map the button resource id to the String value that we want to
    // input when that button is clicked.
    SparseArray<String> keyValues = new SparseArray<>();

    // Our communication link to the EditText
    InputConnection inputConnection;

    @SuppressLint("ClickableViewAccessibility")
    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        // initialize buttons
        LayoutInflater.from(context).inflate(R.layout.keyboard, this, true);
        mButton1 = (Button) findViewById(R.id.button_1);
        mButton2 = (Button) findViewById(R.id.button_2);
        mButton3 = (Button) findViewById(R.id.button_3);
        mButton4 = (Button) findViewById(R.id.button_4);
        mButton5 = (Button) findViewById(R.id.button_5);
        mButton6 = (Button) findViewById(R.id.button_6);
        mButton7 = (Button) findViewById(R.id.button_7);
        mButton8 = (Button) findViewById(R.id.button_8);
        mButton9 = (Button) findViewById(R.id.button_9);
        mButton0 = (Button) findViewById(R.id.button_0);

        mButtonq = (Button) findViewById(R.id.button_q);
        mButtonw = (Button) findViewById(R.id.button_w);
        mButtone = (Button) findViewById(R.id.button_e);
        mButtonr = (Button) findViewById(R.id.button_r);
        mButtont = (Button) findViewById(R.id.button_t);
        mButtony = (Button) findViewById(R.id.button_y);
        mButtonu = (Button) findViewById(R.id.button_u);
        mButtoni = (Button) findViewById(R.id.button_i);
        mButtono = (Button) findViewById(R.id.button_o);
        mButtonp = (Button) findViewById(R.id.button_p);

        mButtona = (Button) findViewById(R.id.button_a);
        mButtons = (Button) findViewById(R.id.button_s);
        mButtond = (Button) findViewById(R.id.button_d);
        mButtonf = (Button) findViewById(R.id.button_f);
        mButtong = (Button) findViewById(R.id.button_g);
        mButtonh = (Button) findViewById(R.id.button_h);
        mButtonj = (Button) findViewById(R.id.button_j);
        mButtonk = (Button) findViewById(R.id.button_k);
        mButtonl = (Button) findViewById(R.id.button_l);

        mButtonz = (Button) findViewById(R.id.button_z);
        mButtonx = (Button) findViewById(R.id.button_x);
        mButtonc = (Button) findViewById(R.id.button_c);
        mButtonv = (Button) findViewById(R.id.button_v);
        mButtonb = (Button) findViewById(R.id.button_b);
        mButtonn = (Button) findViewById(R.id.button_n);
        mButtonm = (Button) findViewById(R.id.button_m);


        mButtonshift = (Button) findViewById(R.id.button_shift);
        mButtonDelete = (Button) findViewById(R.id.button_delete);
        mButtonEnter = (Button) findViewById(R.id.button_enter);
        mButtonAt = (Button) findViewById(R.id.button_at);
        mButtonSpace = (Button) findViewById(R.id.button_space);
        mButtonDash = (Button) findViewById(R.id.button_dash);
        mButtonDot = (Button) findViewById(R.id.button_dot);

        // set button click listeners
        mButton1.setOnClickListener(this);
        keyValues.put(R.id.button_1, "1");
        mButton1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //inputConnection.commitText("1", 1);
                    Log.i("Pressure: " , "Pressure for button 1"  + event.getPressure());
                    // Pressed

                    Log.i("time pressed: ", "Time pressed for button 1 :"+ System.currentTimeMillis() );
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    // Released
                    Log.i("time released: ", "Time released for button 1 :"+ System.currentTimeMillis() );
                }
                return false;
            }
        });

        mButton2.setOnClickListener(this);
        mButton2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.i("Pressure: " , "Pressure for button 2"  + event.getPressure());
                    // Pressed
                    Log.i("time pressed: ", "Time pressed for button 2 :"+ System.currentTimeMillis() );
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    // Released
                    Log.i("time released: ", "Time released for button 2 :"+ System.currentTimeMillis() );
                }
                return false;
            }
        });

        mButton3.setOnClickListener(this);
        mButton3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("Keystroke: ");
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.i("Pressure: " , "Pressure for button 3"  + event.getPressure());
                    myRef.child("Pressure").child("Button 3").push().setValue(event.getPressure());

                    // Pressed
                    Log.i("time pressed: ", "Time pressed for button 3 :"+ System.currentTimeMillis() );
                    myRef.child("Time Pressed:").child("Button 3").push().setValue(System.currentTimeMillis());
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    // Released
                    Log.i("time released: ", "Time released for button 3 :"+ System.currentTimeMillis() );
                    myRef.child("Time Released:").child("Button 3").push().setValue(System.currentTimeMillis());
                }
                return false;
            }
        });

        mButton4.setOnClickListener(this);
        mButton5.setOnClickListener(this);
        mButton6.setOnClickListener(this);
        mButton7.setOnClickListener(this);
        mButton8.setOnClickListener(this);
        mButton9.setOnClickListener(this);
        mButton0.setOnClickListener(this);

        mButtonq.setOnClickListener(this);
        mButtonw.setOnClickListener(this);
        mButtone.setOnClickListener(this);
        mButtonr.setOnClickListener(this);
        mButtont.setOnClickListener(this);
        mButtony.setOnClickListener(this);
        mButtonu.setOnClickListener(this);
        mButtoni.setOnClickListener(this);
        mButtono.setOnClickListener(this);
        mButtonp.setOnClickListener(this);

        mButtona.setOnClickListener(this);
        mButtons.setOnClickListener(this);
        mButtond.setOnClickListener(this);
        mButtonf.setOnClickListener(this);
        mButtong.setOnClickListener(this);
        mButtonh.setOnClickListener(this);
        mButtonj.setOnClickListener(this);
        mButtonk.setOnClickListener(this);
        mButtonl.setOnClickListener(this);

        mButtonz.setOnClickListener(this);
        mButtonx.setOnClickListener(this);
        mButtonc.setOnClickListener(this);
        mButtonv.setOnClickListener(this);
        mButtonb.setOnClickListener(this);
        mButtonn.setOnClickListener(this);
        mButtonm.setOnClickListener(this);

        mButtonDelete.setOnClickListener(this);
        mButtonEnter.setOnClickListener(this);
        mButtonshift.setOnClickListener(this);
        mButtonSpace.setOnClickListener(this);
        mButtonAt.setOnClickListener(this);
        mButtonDash.setOnClickListener(this);
        mButtonDot.setOnClickListener(this);


        // map buttons IDs to input strings
        keyValues.put(R.id.button_1, "1");
        keyValues.put(R.id.button_2, "2");
        keyValues.put(R.id.button_3, "3");
        keyValues.put(R.id.button_4, "4");
        keyValues.put(R.id.button_5, "5");
        keyValues.put(R.id.button_6, "6");
        keyValues.put(R.id.button_7, "7");
        keyValues.put(R.id.button_8, "8");
        keyValues.put(R.id.button_9, "9");
        keyValues.put(R.id.button_0, "0");

        keyValues.put(R.id.button_q, "q");
        keyValues.put(R.id.button_w, "w");
        keyValues.put(R.id.button_e, "e");
        keyValues.put(R.id.button_r, "r");
        keyValues.put(R.id.button_t, "t");
        keyValues.put(R.id.button_y, "y");
        keyValues.put(R.id.button_u, "u");
        keyValues.put(R.id.button_i, "i");
        keyValues.put(R.id.button_o, "o");
        keyValues.put(R.id.button_p, "p");

        keyValues.put(R.id.button_a, "a");
        keyValues.put(R.id.button_s, "s");
        keyValues.put(R.id.button_d, "d");
        keyValues.put(R.id.button_f, "f");
        keyValues.put(R.id.button_g, "g");
        keyValues.put(R.id.button_h, "h");
        keyValues.put(R.id.button_j, "j");
        keyValues.put(R.id.button_k, "k");
        keyValues.put(R.id.button_l, "l");

        keyValues.put(R.id.button_z, "z");
        keyValues.put(R.id.button_x, "x");
        keyValues.put(R.id.button_c, "c");
        keyValues.put(R.id.button_v, "v");
        keyValues.put(R.id.button_b, "b");
        keyValues.put(R.id.button_n, "n");
        keyValues.put(R.id.button_m, "m");

        keyValues.put(R.id.button_enter, "\n");
        keyValues.put(R.id.button_space, " ");
        keyValues.put(R.id.button_at, "@");
        keyValues.put(R.id.button_dash, "-");
        keyValues.put(R.id.button_dot, ".");

    }

    @Override
    public void onClick(View v) {

        // do nothing if the InputConnection has not been set yet
        if (inputConnection == null) return;

        // Delete text or input key value
        // All communication goes through the InputConnection
        if (v.getId() == R.id.button_delete) {
            CharSequence selectedText = inputConnection.getSelectedText(0);
            if (TextUtils.isEmpty(selectedText)) {
                // no selection, so delete previous character
                inputConnection.deleteSurroundingText(1, 0);
            } else {
                // delete the selection
                inputConnection.commitText("", 1);
            }
        } else if(v.getId() == R.id.button_shift){

             isUpper = !isUpper;
            Log.i("KEYCODE_SHIFT", "Current status " + isUpper);
        } else {

            String value = keyValues.get(v.getId());
            char s = value.charAt(0);

            if(isUpper && !Character.isDigit(s)){
                inputConnection.commitText(Character.toString(Character.toUpperCase(s)) , 1);
                isUpper= false;
            }else{
                inputConnection.commitText(value, 1);
                isUpper = false;
            }

        }
    }

    // The activity (or some parent or controller) must give us
    // a reference to the current EditText's InputConnection
    public void setInputConnection(InputConnection ic) {
        this.inputConnection = ic;
    }
}