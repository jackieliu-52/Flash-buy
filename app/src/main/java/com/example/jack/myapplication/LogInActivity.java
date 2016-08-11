package com.example.jack.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import com.mikepenz.iconics.context.IconicsLayoutInflater;

/**
 * Created by Jack on 2016/8/10.
 */
public class LogInActivity extends AppCompatActivity {
    private ImageButton ib_login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflaterCompat.setFactory(getLayoutInflater(), new IconicsLayoutInflater(getDelegate()));
        super.onCreate(savedInstanceState);

        MainActivity.instance = this;

        //do something
        setContentView(R.layout.activity_login);
        ib_login =(ImageButton) findViewById(R.id.ib_login);
        ib_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LogInActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
