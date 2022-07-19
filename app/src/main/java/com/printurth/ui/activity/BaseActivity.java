package com.printurth.ui.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.printurth.SessionManager;

public class BaseActivity extends AppCompatActivity {

    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionManager(this);


    }


}
