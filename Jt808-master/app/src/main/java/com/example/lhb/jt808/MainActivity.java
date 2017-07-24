package com.example.lhb.jt808;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.lhb.tool.Jt808Client;

public class MainActivity extends AppCompatActivity {

    Jt808Client client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
